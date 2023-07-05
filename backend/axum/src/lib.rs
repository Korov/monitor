use log::info;

pub struct Person {
    name: String,
    age: u8,
}

impl Person {
    pub fn new(name: String, age: u8) -> Self {
        Self { name, age }
    }

    pub fn say_hello(&self) {
        println!("Hello, my name is {}.", self.name);
        info!("Hello, my name is {}.", self.name);
    }

    pub fn get_age(&self) -> u8 {
        self.age
    }

    pub fn set_age(&mut self, age: u8) {
        self.age = age;
    }
}
