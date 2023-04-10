import { createRouter, createWebHashHistory } from "vue-router";
import routes from "./routes";
import { websiteTitle } from "@/config";

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes
});

router.afterEach((to) => {
  const { title } = to.meta;
  document.title = title ? `${title} - ${websiteTitle}` : websiteTitle;
});

// 动态增加路由
/*router.addRoute("demo", {
  path: `/demo`,
  name: "demo",
  redirect: {
    name: "aaa"
  },
  component: () => import(`@/index.vue`),
  children: []
});*/

export default router;
