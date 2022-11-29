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

export { Topic, Config }
