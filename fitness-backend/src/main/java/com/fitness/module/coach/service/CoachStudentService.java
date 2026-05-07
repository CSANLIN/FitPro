package com.fitness.module.coach.service;

import com.fitness.module.coach.vo.StudentStatsVO;

import java.util.List;

public interface CoachStudentService {

    List<StudentStatsVO> listStudents(Long coachId);

    StudentStatsVO getStudentDetail(Long coachId, Long userId);
}
