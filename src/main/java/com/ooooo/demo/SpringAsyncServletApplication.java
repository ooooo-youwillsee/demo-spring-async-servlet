package com.ooooo.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@RestController
@SpringBootApplication
public class SpringAsyncServletApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringAsyncServletApplication.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor(new CustomizableThreadFactory("handler"
            + "-"));

    public static void main(String[] args) {
        SpringApplication.run(SpringAsyncServletApplication.class, args);
    }

    /**
     * 普通请求
     *
     * @return
     */
    @GetMapping("/test1")
    public String test1() {
        before();
        process();
        after();
        return "test1";
    }

    /**
     * 异步请求
     *
     * @return
     */
    @GetMapping("/test2")
    public DeferredResult<String> test2() {
        before();
        DeferredResult<String> result = new DeferredResult<>();
        executor.submit(() -> {
            process();
            result.setResult("test2");
        });
        after();
        return result;
    }

    /**
     * 没有返回结果，不是异步请求
     *
     * @return
     */
    @GetMapping("/test3")
    public Runnable test3() {
        before();
        Runnable runnable = this::process;
        after();
        return runnable;
    }

    /**
     * 异步请求
     *
     * @return
     */
    @GetMapping("/test4")
    public Callable<String> test4() {
        before();
        Callable<String> callable = () -> {
            process();
            return "test4";
        };
        after();
        return callable;
    }

    private void before() {
        LOGGER.info("before");
    }

    private void after() {
        LOGGER.info("after");
    }

    private void process() {
        try {
            LOGGER.info("process before");
            TimeUnit.SECONDS.sleep(3);
            LOGGER.info("process after");
        } catch (InterruptedException ignored) {
        }
    }
}
