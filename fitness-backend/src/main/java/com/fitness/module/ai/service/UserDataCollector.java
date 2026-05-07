package com.fitness.module.ai.service;

import com.fitness.common.PageResult;
import com.fitness.module.checkin.service.CheckInService;
import com.fitness.module.checkin.vo.CheckInStatsVO;
import com.fitness.module.membership.service.MembershipService;
import com.fitness.module.membership.vo.MembershipVO;
import com.fitness.module.user.service.BodyRecordService;
import com.fitness.module.user.service.UserService;
import com.fitness.module.user.vo.BodyRecordVO;
import com.fitness.module.user.vo.UserVO;
import com.fitness.module.workout.dto.WorkoutPlanQueryDTO;
import com.fitness.module.workout.dto.WorkoutRecordQueryDTO;
import com.fitness.module.workout.service.WorkoutPlanService;
import com.fitness.module.workout.service.WorkoutRecordService;
import com.fitness.module.workout.vo.WorkoutPlanVO;
import com.fitness.module.workout.vo.WorkoutRecordVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDataCollector {

    private final UserService userService;
    private final BodyRecordService bodyRecordService;
    private final CheckInService checkInService;
    private final MembershipService membershipService;
    private final WorkoutRecordService workoutRecordService;
    private final WorkoutPlanService workoutPlanService;

    @Data
    @AllArgsConstructor
    public static class UserContext {
        private String nickname;
        private String genderText;
        private Integer age;
        private String bodyInfo;
        private String checkinInfo;
        private String membershipInfo;
        private String weeklyTrainingInfo;
        private String recentRecords;
        private String activePlanInfo;
    }

    /**
     * 收集用户所有相关数据，构建 AI 上下文
     */
    public UserContext collect(Long userId) {
        // 1. 用户基本信息
        String nickname = "用户";
        String genderText = "未知";
        Integer age = null;
        try {
            UserVO user = userService.getDetail(userId);
            if (user != null) {
                nickname = user.getNickname();
                if (user.getGender() != null) {
                    genderText = user.getGender() == 1 ? "男" : user.getGender() == 2 ? "女" : "未知";
                }
                if (user.getBirthday() != null) {
                    age = Period.between(user.getBirthday(), LocalDate.now()).getYears();
                }
            }
        } catch (Exception e) {
            // 用户信息获取失败，使用默认值
        }

        // 2. 身体数据
        String bodyInfo = "暂无";
        try {
            BodyRecordVO body = bodyRecordService.getLatest(userId);
            if (body != null) {
                bodyInfo = String.format("体重%.1fkg", nullSafe(body.getWeight()));
                if (body.getHeight() != null) bodyInfo += String.format("，身高%.1fcm", body.getHeight());
                if (body.getBodyFat() != null) bodyInfo += String.format("，体脂率%.1f%%", body.getBodyFat());
                if (body.getBmi() != null) bodyInfo += String.format("，BMI=%.1f", body.getBmi());
            }
        } catch (Exception e) {
            // 忽略
        }

        // 3. 签到统计
        String checkinInfo = "暂无";
        try {
            CheckInStatsVO stats = checkInService.getStats(userId);
            if (stats != null) {
                checkinInfo = String.format("本月签到%d天，连续签到%d天", stats.getMonthCount(), stats.getStreakDays());
            }
        } catch (Exception e) {
            // 忽略
        }

        // 4. 会籍信息
        String membershipInfo = "暂无";
        try {
            MembershipVO membership = membershipService.getActiveMembership(userId);
            if (membership != null) {
                membershipInfo = String.format("%s(%s)", membership.getCardName(),
                        "ACTIVE".equals(membership.getStatus()) ? "有效" : membership.getStatus());
                if (membership.getRemainingDays() != null) {
                    membershipInfo += String.format("，剩余%d天", membership.getRemainingDays());
                }
                if (membership.getRemainingTimes() != null) {
                    membershipInfo += String.format("，剩余%d次", membership.getRemainingTimes());
                }
            }
        } catch (Exception e) {
            // 忽略
        }

        // 5. 本周训练统计
        String weeklyTrainingInfo = "暂无";
        try {
            Map<String, Object> weeklyStats = workoutRecordService.weeklyStats(userId);
            if (weeklyStats != null) {
                Object count = weeklyStats.get("weeklyCount");
                Object volume = weeklyStats.get("weeklyVolume");
                weeklyTrainingInfo = String.format("本周训练%d次，总训练量%skg",
                        count instanceof Number ? ((Number) count).intValue() : 0,
                        volume instanceof Number ? ((Number) volume).toString() : "0");
            }
        } catch (Exception e) {
            // 忽略
        }

        // 6. 近期训练记录（最近5条）
        String recentRecords = "暂无";
        try {
            WorkoutRecordQueryDTO query = new WorkoutRecordQueryDTO();
            query.setPageNum(1);
            query.setPageSize(5);
            PageResult<WorkoutRecordVO> page = workoutRecordService.pageList(query, userId);
            if (page != null && page.getList() != null && !page.getList().isEmpty()) {
                recentRecords = page.getList().stream()
                        .map(r -> String.format("%s(%dmin/%dkg)",
                                r.getName() != null ? r.getName() : "训练",
                                nullSafeInt(r.getDurationMinutes()),
                                nullSafeInt(r.getTotalVolume())))
                        .collect(Collectors.joining("，"));
            }
        } catch (Exception e) {
            // 忽略
        }

        // 7. 训练计划
        String activePlanInfo = "暂无";
        try {
            WorkoutPlanQueryDTO planQuery = new WorkoutPlanQueryDTO();
            planQuery.setStatus("ACTIVE");
            planQuery.setPageNum(1);
            planQuery.setPageSize(1);
            PageResult<WorkoutPlanVO> plans = workoutPlanService.pageList(planQuery, userId);
            if (plans != null && plans.getList() != null && !plans.getList().isEmpty()) {
                WorkoutPlanVO plan = plans.getList().get(0);
                activePlanInfo = String.format("%s(进行中)", plan.getName());
                if (plan.getDayCount() != null) {
                    activePlanInfo += String.format("，每周%d练", plan.getDayCount());
                }
            }
        } catch (Exception e) {
            // 忽略
        }

        return new UserContext(nickname, genderText, age, bodyInfo, checkinInfo,
                membershipInfo, weeklyTrainingInfo, recentRecords, activePlanInfo);
    }

    private static double nullSafe(Number n) {
        return n != null ? n.doubleValue() : 0;
    }

    private static int nullSafeInt(Integer n) {
        return n != null ? n : 0;
    }
}
