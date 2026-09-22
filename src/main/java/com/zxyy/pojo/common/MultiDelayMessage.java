package com.zxyy.pojo.common;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MultiDelayMessage<T> {
    //消息体
    private T data;
    //记录延时消息的集合
    private List<Long> delayMillis;

    public MultiDelayMessage() {
    }

    //使用默认延时时间
    public MultiDelayMessage(T data){
        this.data = data;
        //list.of返回的是不可变集合 所以需要使用new arraylist包装一下
        this.delayMillis = new ArrayList<>(List.of(
                10_000L,        // 10s
                20_000L,        // 20s
                30_000L,        // 30s
                60_000L,        // 1min
                2 * 60_000L,    // 2min
                3 * 60_000L,    // 3min
                4 * 60_000L,    // 4min
                4 * 60_000L     // 4min
        ));
    }

    public MultiDelayMessage(T data,List<Long> delayMillis){
        this.data = data;
        this.delayMillis = delayMillis;
    }

    public static <T> MultiDelayMessage<T> of(T data, Long ... delayMillis){
        return new MultiDelayMessage<>(data, CollUtil.newArrayList(delayMillis));
    }

    //获取并移除下一个延迟时间 返回队列中第一个延时时间
    public Long removeNextDelay(){
        return delayMillis.remove(0);
    }

    //是否还有下一个延时时间
    public boolean hasNextDelay(){
        return !delayMillis.isEmpty();
    }
}
