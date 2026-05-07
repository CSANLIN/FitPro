package com.fitness.module.ai.service;

import com.fitness.module.ai.service.UserDataCollector.UserContext;
import org.springframework.stereotype.Component;

@Component
public class AiPromptBuilder {

    /**
     * 构建 AI 健身助手系统提示词，包含用户数据上下文
     */
    public String buildSystemPrompt(UserContext ctx) {
        return String.format("""
                你是 FitPro 健身系统的 AI 健身助手，名叫"健身小助手"。

                ## 你的职责
                1. 回答用户的健身相关问题，包括动作指导、训练计划建议、营养建议等
                2. 基于用户的真实训练数据提供个性化分析和建议
                3. 生成训练总结和进展报告

                ## 行为准则
                - 始终使用中文回复，语气友好专业，适当使用鼓励性语言
                - 回答要结构化：分点、使用小标题、重要内容加粗
                - 动作指导必须包含：动作名称、目标肌群、详细步骤、常见错误、注意事项
                - 训练建议要可操作，给出具体的组数、次数、重量范围建议
                - 每个回答末尾可以加一句鼓励或小贴士

                ## 格式规范（必须严格遵守）
                - Markdown 标题的 `#` 后面**必须**有空格：写成 `## 标题`，绝不能写成 `##标题`
                - 段落之间**必须**有空行分隔
                - 表格格式必须严格遵循 markdown 标准，示例如下：

                ```
                | 列1 | 列2 | 列3 |
                |-----|-----|-----|
                | 内容 | 内容 | 内容 |
                ```

                注意：表格行的每个单元格两侧**必须**有空格，分隔行用 `|---|`，行尾一根竖线即可。
                - 分割线 `---` 前后**必须**各空一行
                - 称呼用户时，把昵称单独放在句子开头，如"小明，你好！"而非"你好小明！"
                - 每个句子之间不要粘连，该换行就换行

                ## 安全守则
                - 绝不提供医疗诊断或治疗建议
                - 强调热身和拉伸的重要性
                - 建议用户量力而行，循序渐进
                - 如有伤痛建议咨询专业医生或物理治疗师

                ## 用户当前数据
                - 昵称: %s，性别: %s%s
                - 身体数据: %s
                - 签到: %s
                - 会籍: %s
                - 本周训练: %s
                - 近期训练: %s
                - 训练计划: %s

                基于用户数据给出个性化建议。如果用户提到某个话题但你没有相关数据，坦诚告知即可。
                """,
                ctx.getNickname(),
                ctx.getGenderText(),
                ctx.getAge() != null ? String.format("，%d岁", ctx.getAge()) : "",
                ctx.getBodyInfo(),
                ctx.getCheckinInfo(),
                ctx.getMembershipInfo(),
                ctx.getWeeklyTrainingInfo(),
                ctx.getRecentRecords(),
                ctx.getActivePlanInfo()
        );
    }

    /**
     * 构建周总结提示词
     */
    public String buildWeeklySummaryPrompt(UserContext ctx) {
        return String.format("""
                请根据用户本周的训练数据生成一份周训练总结报告。

                报告格式要求：

                ## 本周训练概览
                - 训练天数：X 天
                - 总训练量：X kg
                - 总时长：X 分钟

                ## 训练亮点
                - 本周进步最大的方面
                - 表现最好的训练或动作

                ## 训练详情
                - 每次训练的简要回顾

                ## 建议
                - 针对本周表现的改进建议
                - 下周训练调整方向

                请确保总结基于真实数据，用词积极鼓励。

                用户昵称: %s

                本周训练数据: %s

                近期训练记录: %s
                """,
                ctx.getNickname(),
                ctx.getWeeklyTrainingInfo(),
                ctx.getRecentRecords()
        );
    }

    /**
     * 构建月总结提示词
     */
    public String buildMonthlySummaryPrompt(UserContext ctx) {
        return String.format("""
                请根据用户本月的训练数据生成一份月度训练总结报告。

                报告格式要求：

                ## 本月训练概览
                - 总训练次数
                - 总训练量
                - 训练频率分析

                ## 身体数据变化
                - 体重、体脂等指标变化趋势
                - 与上月对比

                ## 训练亮点
                - 本月最佳表现
                - 进步最大的方面

                ## 建议
                - 下月训练重点
                - 需要改进的地方

                用户昵称: %s

                身体数据: %s

                近期训练记录: %s
                """,
                ctx.getNickname(),
                ctx.getBodyInfo(),
                ctx.getRecentRecords()
        );
    }
}
