interface Topic {
  name: string
  partition: number
  replica: number
  internal: boolean
}

interface Config {
  id: number
  name: string
  broker: string
}

interface Broker {
  id: number
  host: string
  port: number
}

interface Partition {
  beginningOffset: number
  endOffset: number
  isr: Broker[]
  leader: Broker[]
  replicas: Broker[]
  partition: number
}

interface Consumer {
  endOffset: number
  lag: number
  offset: number
  partition: number
  topic: string
}

export { Topic, Config, Partition, Broker, Consumer }
