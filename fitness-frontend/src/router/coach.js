const CoachLayout = () => import('@/layout/CoachLayout.vue')
const CoachSchedule = () => import('@/views/coach/CoachScheduleView.vue')
const CoachAnalysis = () => import('@/views/coach/CoachAnalysisView.vue')
const CoachStudent = () => import('@/views/coach/CoachStudentView.vue')

export default [
  {
    path: '/coach',
    component: CoachLayout,
    meta: {
      title: '教练端',
      requiresAuth: true,
      roles: ['ROLE_COACH']
    },
    redirect: '/coach/schedule',
    children: [
      {
        path: 'schedule',
        name: 'CoachSchedule',
        component: CoachSchedule,
        meta: { title: '课程编排', icon: 'Calendar' }
      },
      {
        path: 'analysis',
        name: 'CoachAnalysis',
        component: CoachAnalysis,
        meta: { title: '授课分析', icon: 'DataAnalysis' }
      },
      {
        path: 'students',
        name: 'CoachStudent',
        component: CoachStudent,
        meta: { title: '学员分析', icon: 'User' }
      }
    ]
  }
]
