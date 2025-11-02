<template>
  <div class="page-wrap">
    <HeaderBar :currentName="'添加会员'" />
    <div class="form">
      <div class="form-item">
        <div class="label">用户名</div>
        <div class="input">
          <van-field
            class="input-field"
            v-model="form.username"
            placeholder="请输入用户名(强制英文和数字混合)"
            :error="!!errors.username"
            :error-message="errors.username"
            clearable
            @blur="validateUsername(form.username)"
          />
        </div>
      </div>
      <div class="form-item">
        <div class="label">登录密码</div>
        <div class="input">
          <van-field
            class="input-field"
            v-model="form.password"
            type="password"
            placeholder="请输入登录密码"
            :error="!!errors.password"
            :error-message="errors.password"
            clearable
            @blur="validatePassword(form.password)"
          />
        </div>
      </div>
      <div class="form-item">
        <div class="label">确认密码</div>
        <div class="input">
          <van-field
            class="input-field"
            v-model="form.confirmPassword"
            type="password"
            placeholder="请输入确认密码"
            :error="!!errors.confirmPassword"
            :error-message="errors.confirmPassword"
            clearable
            @blur="validateConfirmPassword(form.confirmPassword)"
          />
        </div>
      </div>
      <van-button 
        block 
        class="submit" 
        :loading="loading" 
        @click="handleSubmit"
      >
        确认添加
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import HeaderBar from '@/components/HeaderBar/index.vue'
import { addMember } from '@/api/plug'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const errors = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

/**
 * 验证用户名（必须包含英文和数字）
 */
const validateUsername = (username) => {
  if (!username) {
    errors.username = '请输入用户名'
    return false
  }
  // 必须包含英文字母和数字
  const hasLetter = /[a-zA-Z]/.test(username)
  const hasNumber = /[0-9]/.test(username)
  if (!hasLetter || !hasNumber) {
    errors.username = '用户名必须包含英文字母和数字'
    return false
  }
  // 只能包含英文字母和数字
  if (!/^[a-zA-Z0-9]+$/.test(username)) {
    errors.username = '用户名只能包含英文字母和数字'
    return false
  }
  errors.username = ''
  return true
}

/**
 * 验证密码
 */
const validatePassword = (password) => {
  if (!password) {
    errors.password = '请输入登录密码'
    return false
  }
  if (password.length < 5) {
    errors.password = '密码长度不能少于5位'
    return false
  }
  errors.password = ''
  return true
}

/**
 * 验证确认密码
 */
const validateConfirmPassword = (confirmPassword) => {
  if (!confirmPassword) {
    errors.confirmPassword = '请输入确认密码'
    return false
  }
  if (confirmPassword !== form.password) {
    errors.confirmPassword = '两次输入的密码不一致'
    return false
  }
  errors.confirmPassword = ''
  return true
}

/**
 * 表单验证
 */
const validateForm = () => {
  const validUsername = validateUsername(form.username)
  const validPassword = validatePassword(form.password)
  const validConfirmPassword = validateConfirmPassword(form.confirmPassword)
  return validUsername && validPassword && validConfirmPassword
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  // 清除之前的错误
  errors.username = ''
  errors.password = ''
  errors.confirmPassword = ''
  
  if (!validateForm()) {
    return
  }
  
  loading.value = true
  try {
    const res = await addMember({
      loginName: form.username,
      loginPassword: form.password
    })
    
    if (res.code === '200' || res.code === 200) {
      showToast('添加成功')
      // 清空表单
      form.username = ''
      form.password = ''
      form.confirmPassword = ''
      // 延迟返回上一页
      setTimeout(() => {
        router.back()
      }, 1500)
    } else {
      showToast(res.msg || '添加失败')
    }
  } catch (error) {
    console.error('添加会员失败:', error)
    showToast(error.response?.data?.msg || '添加失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.page-wrap {
  min-height: 100vh;
  background: var(--ex-default-background-color);
}

.form {
  padding: 12px;
  
  .form-item {
    margin-bottom: 20px;
    
    .label {
      font-size: 14px;
      color: var(--ex-default-font-color);
      margin-bottom: 10px;
      padding-left: 10px;
    }
    
    .input {
      background: var(--ex-passive-background-color);
      border-radius: 4px;
      overflow: hidden;
      
      .input-field {
        background: transparent;
        
        :deep(.van-field__control) {
          padding-left: 10px;
          color: var(--ex-default-font-color);
        }
        
        :deep(.van-field__label) {
          color: var(--ex-default-font-color);
        }
      }
    }
  }
  
  .submit {
    margin-top: 50px;
    background: var(--ex-div-bgColor1);
    color: var(--ex-font-color);
    border: none;
    height: 44px;
    border-radius: 4px;
  }
}
</style>


