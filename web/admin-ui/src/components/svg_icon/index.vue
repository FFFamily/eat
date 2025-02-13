<script setup lang="ts">
let props = defineProps({
  name: String
})
console.log(props.name)
</script>

<template>
  <div>
<!--    <svg>-->
<!--      <use :href="name"></use>-->
<!--    </svg>-->
    <img src="../../assets/vue.svg" class="logo vue" alt="Vue logo">
  </div>
</template>

<style scoped>

</style>