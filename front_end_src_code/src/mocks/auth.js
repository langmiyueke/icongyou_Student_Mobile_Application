// Mock auth helpers
export function mockLogin (payload) {
  return {
    token: 'mock-token-1234567890',
    user: {
      id: 999,
      user_name: payload?.user_name || payload?.name || 'mockuser',
      nick_name: payload?.nick_name || '测试用户',
      phone: payload?.phone || '13800000000',
      avatar: ''
    }
  }
}

export function mockRegister (payload) {
  const userName = payload?.user_name || payload?.name || 'newuser'
  const nick = payload?.nick_name || '新用户'
  // mock tenant id lookup: if tenant_name provided, return a fake tenant_id
  const tenantId = payload?.tenant_name ? 42 : null
  return { success: true, message: '注册成功', user: { id: 1000, user_name: userName, nick_name: nick, phone: payload?.phone, tenant_id: tenantId } }
}
