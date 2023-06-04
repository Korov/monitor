import { createI18n } from 'vue-i18n'

const messages = {
  zh: {
    kafka: {
      queryError: '查询所有kafka环境失败，',
      clusterName: '集群名称',
      address: '地址',
      operation: '操作',
      delete: '删除',
      addEnvironment: '添加环境',
      addKafkaAddress: '添加kafka地址',
      name: '名称',
      cancel: '取 消',
      confirm: '确 定',
    },
  },
  en: {
    kafka: {
      queryError: 'Query all kafka environment failed,',
      clusterName: 'Cluster Name',
      address: 'Address',
      operation: 'Operation',
      delete: 'Delete',
      addEnvironment: 'Add Environment',
      addKafkaAddress: 'Add Kafka Address',
      name: 'Name',
      cancel: 'Cancel',
      confirm: 'Confirm',
    },
  },
}
const i18n = createI18n({
  locale: 'zh',
  messages,
})

export default i18n
