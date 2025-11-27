<template>
  <div class="page-auth">
    <div class="auth-card">
      <img :src="logo" alt="logo" class="logo" />
      <h2 class="title">注册</h2>
      <p class="subtitle">成为爱从游的一员，开启项目化学习</p>

      <van-form @submit="onSubmit">
        <van-field v-model="form.tenant_name" name="tenant_name" placeholder="学校（作为租户名）">
          <template #right-icon>
            <button type="button" class="clear-svg-btn" v-if="form.tenant_name" @click="form.tenant_name = ''" aria-label="清除学校（租户名）">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#409EFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </template>
        </van-field>

          <van-field v-model="form.user_name" name="user_name" placeholder="姓名">
            <template #right-icon>
              <button type="button" class="clear-svg-btn" v-if="form.user_name" @click="form.user_name = ''" aria-label="清除用户名">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#409EFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              </button>
            </template>
          </van-field>

          <van-field v-model="form.nick_name" name="nick_name" placeholder="昵称">
            <template #right-icon>
              <button type="button" class="clear-svg-btn" v-if="form.nick_name" @click="form.nick_name = ''" aria-label="清除昵称">
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

        <van-field v-model="form.password" name="password" placeholder="不少于6位">
          <template #right-icon>
            <button type="button" class="clear-svg-btn" v-if="form.password" @click="form.password = ''" aria-label="清除密码">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#409EFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </template>
        </van-field>

        <van-field v-model="form.confirm" name="confirm" placeholder="再次输入密码">
          <template #right-icon>
            <button type="button" class="clear-svg-btn" v-if="form.confirm" @click="form.confirm = ''" aria-label="清除确认密码">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#409EFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </template>
        </van-field>

        <div class="actions">
          <van-button type="primary" block native-type="submit">注册</van-button>
        </div>

        <div class="links">
          <router-link to="/login">已有账号？去登录</router-link>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { showFailToast, showSuccessToast } from 'vant'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import logo from '@/assets/logo.png'

export default {
  name: 'Register',
  setup () {
    const form = ref({ tenant_name: '', user_name: '', nick_name: '', phone: '', password: '', confirm: '' })

    const validate = () => {
      if (!form.value.tenant_name) { showFailToast('请输入学校（作为租户名）'); return false }
      if (!form.value.user_name) { showFailToast('请输入姓名'); return false }
      if (!form.value.nick_name) { showFailToast('请输入昵称'); return false }
      if (!form.value.phone) { showFailToast('请输入手机号'); return false }
      if (!form.value.password || form.value.password.length < 6) { showFailToast('密码至少6位'); return false }
      if (form.value.password !== form.value.confirm) { showFailToast('两次密码不一致'); return false }
      return true
    }

    const router = useRouter()

    const onSubmit = async () => {
      if (!validate()) return
      try {
        // TODO: 调用后端接口 — 注册入口，确认必填字段与后端返回行为
        // 按新约定发送注册信息：包含用户名、昵称、手机号、密码与 user_type(1=学生)
        // 新增字段 `tenant_name`：后端将以该租户名查询租户表并获取 tenant_id，然后把 tenant_id 存到 User 表中。
        // 后端将以 `tenant_name` 查询租户表并获取 tenant_id，并写入 User.tenant_id。
        await register({
          user_name: form.value.user_name,
          nick_name: form.value.nick_name,
          phone: form.value.phone,
          pwd: form.value.password,
          user_type: 1,
          tenant_name: form.value.tenant_name
        })
        showSuccessToast('注册成功，请登录')
        window.setTimeout(() => { router.replace({ path: '/login' }) }, 600)
      } catch (err) {
        showFailToast(err?.message || '注册失败')
      }
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
  max-width: 520px;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 6px 18px rgba(30,40,70,0.08);
}
.auth-card ::v-deep .van-field { margin-top: 16px; position: relative }
.auth-card ::v-deep .van-field__control,
.auth-card ::v-deep .van-field__body { padding-right: 44px }
.auth-card ::v-deep .clear-svg-btn { position: absolute; right: 8px; top: 50%; transform: translateY(-50%); background: transparent; border: none; padding: 4px; display: inline-flex; align-items: center; justify-content: center; cursor: pointer }
.auth-card ::v-deep .clear-svg-btn svg { display: block }
.logo { display:block; width:84px; height:84px; margin:0 auto 10px; }
.title { text-align:center; margin:0; font-size:22px; color:#111827; font-weight:600 }
.subtitle { text-align:center; margin:6px 0 18px; color:#6b7280; font-size:14px }
.actions { margin-top: 14px; }
.links { margin-top: 14px; text-align: center; color:#6b7280 }
.password-row { position: relative }
.pw-toggle { position: absolute; right: 10px; top: 8px; background: transparent; border: none; color: #4b5563; cursor: pointer; padding:4px }
.password-row { position: relative }
.pw-toggle { position: absolute; right: 10px; top: 8px; background: transparent; border: none; color: #4b5563; cursor: pointer; padding:4px }
</style>
