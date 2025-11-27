import { createRouter, createWebHistory } from 'vue-router'
// auth 模块下的登录/注册页面
const Login = () => import(/* webpackChunkName: "login" */ '../views/auth/Login.vue')
const Register = () => import(/* webpackChunkName: "register" */ '../views/auth/Register.vue')

const Courses = () => import(/* webpackChunkName: "courses" */ '../views/courses/Courses.vue')
const CourseDetail = () => import(/* webpackChunkName: "course-detail" */ '../views/courses/components/CourseDetail.vue')
const TaskCenter = () => import(/* webpackChunkName: "task-center" */ '../views/courses/components/TaskCenter.vue')

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/courses',
    name: 'home',
    component: Courses
  },
  {
    path: '/courses/:id',
    name: 'course',
    component: CourseDetail,
    props: true
  },
  {
    path: '/courses/:id/tasks',
    name: 'course-tasks',
    component: CourseDetail,
    props: true
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  },
  {
    path: '/register',
    name: 'register',
    component: Register
  }
  ,
  {
    path: '/tasks',
    name: 'tasks',
    component: TaskCenter
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// // 简单路由守卫：如果已登录（存在 token），阻止访问登录/注册页并重定向到 /courses
// router.beforeEach((to, from, next) => {
//   try {
//     const token = localStorage.getItem('token')
//     if (token && (to.path === '/login' || to.path === '/register')) {
//       next({ path: '/courses' })
//       return
//     }
//   } catch (err) {
//     // ignore
//   }
//   next()
// })

export default router
