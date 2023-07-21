#[cfg(test)]
mod tests {
    use std::io;

    use chrono::Local;
    use tracing::{debug, error, event, info, instrument, span, trace, warn, Level};
    use tracing_subscriber::{
        fmt::{self, format::Writer, time::FormatTime},
        prelude::__tracing_subscriber_SubscriberExt,
        util::SubscriberInitExt,
    };

    // 用来格式化日志的输出时间格式
    struct LocalTimer;

    impl FormatTime for LocalTimer {
        fn format_time(&self, w: &mut Writer<'_>) -> std::fmt::Result {
            write!(w, "{}", Local::now().format("%FT%T%.3f"))
        }
    }

    #[test]
    fn appender_test() {
        let file_appender = tracing_appender::rolling::daily("logs", "monitor.log");
        let (non_blocking, _guard) = tracing_appender::non_blocking(file_appender);
        // 初始化并设置日志格式(定制和筛选日志)
        let file_layer = fmt::layer()
            .event_format(fmt::format().with_timer(LocalTimer))
            .with_writer(io::stdout)
            .with_writer(non_blocking)
            .with_ansi(false)
            .with_line_number(true)
            .with_thread_names(true);

        let console_layer = fmt::layer()
            .event_format(fmt::format().with_timer(LocalTimer))
            .with_writer(io::stdout)
            .with_ansi(true)
            .with_line_number(true)
            .with_thread_names(true);

        tracing_subscriber::registry().with(file_layer).with(console_layer).init();

        test_trace(33);
        trace!("tracing-trace");
        debug!("tracing-debug");
        info!("tracing-info");
        warn!("tracing-warn");
        error!("tracing-error");
    }

    // 通过instrument属性，直接让整个函数或方法进入span区间，且适用于异步函数async fn fn_name(){}
    // 参考：https://docs.rs/tracing/latest/tracing/attr.instrument.html
    // #[tracing::instrument(level = "info")]
    #[instrument]
    fn test_trace(n: i32) {
        // #[instrument]属性表示函数整体在一个span区间内，因此函数内的每一个event信息中都会额外带有函数参数
        // 在函数中，只需发出日志即可
        event!(Level::TRACE, answer = 42, "trace2: test_trace");
        trace!(answer = 42, "trace1: test_trace");
        info!(answer = 42, "info1: test_trace");
    }

    #[test]
    fn fmt_test() {
        let format = tracing_subscriber::fmt::format()
            .with_level(true)
            .with_target(true)
            .with_timer(LocalTimer);

        // 初始化并设置日志格式(定制和筛选日志)
        let fmt_layer = fmt::layer()
            .event_format(format)
            .with_writer(io::stdout)
            .with_ansi(true)
            .with_line_number(true)
            .with_thread_names(true);
        // 只有注册 subscriber 后， 才能在控制台上看到日志输出, 直接初始化，采用默认的Subscriber，默认只输出INFO、WARN、ERROR级别的日志
        tracing_subscriber::registry().with(fmt_layer).init();

        let s = span!(Level::TRACE, "my span");
        // 没进入 span，因此输出日志将不会带上 span 的信息
        event!(target: "app_events", Level::INFO, "something has happened 1!");

        // 进入 span ( 开始 )
        let _enter = s.enter();
        // 没有设置 target 和 parent
        // 这里的对象位置分别是当前的 span 名和模块名
        event!(Level::INFO, "something has happened 2!");
        // 设置了 target
        // 这里的对象位置分别是当前的 span 名和 target
        event!(target: "app_events",Level::INFO, "something has happened 3!");

        let span = span!(Level::TRACE, "my span 1");
        // 这里就更为复杂一些，留给大家作为思考题
        event!(parent: &span, Level::INFO, "something has happened 4!");

        // 格式化输出
        let question = "the ultimate question of life, the universe, and everything";
        let answer = 42;
        event!(
            Level::DEBUG,
            question.answer = answer,
            question.tricky = true,
            "the answer to {} is {}.",
            question,
            answer
        );
    }
}
