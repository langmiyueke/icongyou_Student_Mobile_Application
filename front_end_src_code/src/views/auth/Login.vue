<template>
  <div class="page-auth">
    <div class="auth-card">
      <img :src="logo" alt="logo" class="logo" />
      <h2 class="title">登录</h2>
      <p class="subtitle">渐积跬步，从游而学 </p>

      <van-form @submit="onSubmit">
        <van-field v-model="form.name" name="name" placeholder="姓名">
          <template #right-icon>
            <button type="button" class="clear-svg-btn" v-if="form.name" @click="form.name = ''" aria-label="清除姓名">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#409EFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </template>
        </van-field>
        <van-field v-model="form.phone" name="phone" placeholder="手机号">
          <template #right-icon>
            <button type="button" class="clear-svg-btn" v-if="form.phone" @click="form.phone = ''" aria-label="清除手机号">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#409EFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </template>
        </van-field>

        <van-field v-model="form.password" name="password" type="password" placeholder="请输入密码">
          <template #right-icon>
            <button type="button" class="clear-svg-btn" v-if="form.password" @click="form.password = ''" aria-label="清除密码">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#409EFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </template>
        </van-field>

        <div class="extras">
          <router-link to="/forgot" class="forgot">忘记密码？</router-link>
        </div>

        <div class="actions">
          <van-button type="primary" block native-type="submit">登录</van-button>
        </div>

        <div class="links">
          <router-link to="/register">没有账号？立即注册</router-link>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showFailToast, showSuccessToast } from 'vant'
import { login } from '@/api/auth'
import { setAuthToken } from '@/api'
import logo from '@/assets/logo.png'

export default {
  name: 'Login',
  setup () {
    const form = ref({ name: '', phone: '', password: '' })

    const validate = () => {
      if (!form.value.name) {
        showFailToast('请输入姓名')
        return false
      }
      if (!form.value.phone) {
        showFailToast('请输入手机号')
        return false
      }
      if (!form.value.password) {
        showFailToast('请输入密码')
        return false
      }
      return true
    }

    const router = useRouter()
    const route = useRoute()

    const onSubmit = async () => {
      if (!validate()) return
      try {
        // TODO: 前端默认点击登录直接跳转至课程页，后续根据需求调整
        router.replace('/courses')
      } catch (err) {
        showFailToast(err?.message || '登录失败')
      }

      //   // 根据新约定使用姓名+手机号+密码进行登录：
      //   const payload = { user_name: form.value.name, phone: form.value.phone, pwd: form.value.password }
      //   // TODO: 调用后端接口 — 登录入口，确认返回值包含 token 并处理后端错误码
      //   const res = await login(payload)
      //   // res 应为 { token, user }
      //   const token = res?.token || res?.data?.token
      //   if (!token) throw new Error('未收到 token')
      //   setAuthToken(token)
      //   showSuccessToast('登录成功')
      //   const redirect = route.query.redirect || '/courses'
      //   router.replace({ path: String(redirect) })
      // } catch (err) {
      //   showFailToast(err?.message || '登录失败')
      // }
    }

    return { form, onSubmit, logo }
  }
}
</script>

<style scoped>
.page-auth {
  padding: 24px 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 40px);
  background: linear-gradient(180deg, #f5f7fb 0%, #ffffff 100%);
}
.auth-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 6px 18px rgba(30,40,70,0.08);
}
.auth-card ::v-deep .van-field { margin-top: 16px }
.auth-card ::v-deep .van-field { position: relative }
.auth-card ::v-deep .van-field__control,
.auth-card ::v-deep .van-field__body { padding-right: 44px }
.auth-card ::v-deep .clear-svg-btn { position: absolute; right: 8px; top: 50%; transform: translateY(-50%); }
.logo { display:block; width:84px; height:84px; margin:0 auto 10px; }
.title { text-align:center; margin:0; font-size:22px; color:#111827; font-weight:600 }
.subtitle { text-align:center; margin:6px 0 18px; color:#6b7280; font-size:14px }
.actions { margin-top: 14px; }
.links { margin-top: 14px; text-align: center; color:#6b7280 }
.extras { display:flex; justify-content:flex-end; align-items:center; margin-top:8px }
.forgot { color: #409EFF; font-size:13px }
.password-row { position: relative }
.clear-svg-btn { background: transparent; border: none; padding: 4px; display: inline-flex; align-items: center; justify-content: center; cursor: pointer }
.clear-svg-btn svg { display: block }
</style>
