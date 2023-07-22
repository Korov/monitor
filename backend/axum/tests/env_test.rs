#[cfg(test)]
mod tests {
    use std::{
        env,
        fs::File,
        io::{BufRead, BufReader},
    };

    use log::info;
    use monitor_lib::init_tracing;

    #[tokio::test]
    async fn env_test() {
        init_tracing().await;

        // 从文件中读取环境变量并将其设置为系统变量
        if let Ok(file) = File::open(".env") {
            let reader = BufReader::new(file);
            for line in reader.lines() {
                if let Ok(line) = line {
                    let mut parts = line.split('=');
                    if let Some(key) = parts.next() {
                        if let Some(value) = parts.next() {
                            env::set_var(key, value);
                            info!("key:{}, value:{}", key, value);
                        }
                    }
                }
            }
        }

        // 获取监听地址
        let log_file_name = env::var("log_file_name").unwrap();
        info!("addr:{}", log_file_name);
    }
}
