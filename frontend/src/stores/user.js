import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const accessToken = ref(localStorage.getItem('accessToken') || '')

  const isLoggedIn = computed(() => !!accessToken.value)
  const userId = computed(() => userInfo.value?.id)
  const username = computed(() => userInfo.value?.username)
  const realName = computed(() => userInfo.value?.realName || userInfo.value?.username)

  function setLogin(data) {
    accessToken.value = data.accessToken
    userInfo.value = data.user
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('userInfo', JSON.stringify(data.user))
  }

  function logout() {
    accessToken.value = ''
    userInfo.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('userInfo')
  }

  function updateUserInfo(user) {
    userInfo.value = user
    localStorage.setItem('userInfo', JSON.stringify(user))
  }

  return {
    userInfo,
    accessToken,
    isLoggedIn,
    userId,
    username,
    realName,
    setLogin,
    logout,
    updateUserInfo
  }
})
