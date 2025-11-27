<!-- components/PersonalTaskPanel.vue -->
<template>
  <div class="personal-task-panel">
    <h3>个人任务面板</h3>
    <p>任务ID: {{ taskId }}</p>
    <el-empty description="个人任务功能开发中" />
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  taskId: {
    type: String,
    required: true
  }
})
</script>