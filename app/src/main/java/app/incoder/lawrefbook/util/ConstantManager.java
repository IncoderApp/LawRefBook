package app.incoder.lawrefbook.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * ConstantManager
 *
 * @author Jerry Xu
 * @since 2025/6/29 15:00
 */
public class ConstantManager {

    public static String DEFAULT_CATEGORY = "default_category";

    /**
     * thread full parameters
     * corePoolSize：5
     * maximumPoolSize：10
     * keepAliveTime：survival 60 minutes
     * workQueue：LinkedBlockingQueue
     * threadFactory：defaultThreadFactory
     * handlerPolicy：AbortPolicy
     */
    public static final ThreadPoolExecutor POOL_EXECUTOR =
            new ThreadPoolExecutor(5,
                    10
                    , 60
                    , TimeUnit.MINUTES
                    , new LinkedBlockingQueue<>()
                    , Executors.defaultThreadFactory()
                    , new ThreadPoolExecutor.AbortPolicy());

    public static <T> int findLastIndex(List<T> list, Predicate<T> predicate) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (predicate.test(list.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static <T> List<T> getElementsBefore(List<T> list, T target) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        int index = list.indexOf(target);
        if (index <= 0) {
            return Collections.emptyList();
        }

        return new ArrayList<>(list.subList(0, index));
    }

    public static <T> List<T> replaceOrder(List<T> listA, List<T> listB) {
        if (listA == null || listA.isEmpty()) {
            return new ArrayList<>();
        }
        if (listB == null || listB.isEmpty()) {
            return new ArrayList<>(listA);
        }
        // 创建一个 Set 来快速查找 B 中的元素
        Set<T> setB = new HashSet<>(listB);
        // 创建结果列表
        // 先添加 B 中的元素（按 B 的顺序）
        List<T> result = new ArrayList<>(listB);

        // 再添加 A 中但不在 B 中的元素（保持 A 的原始顺序）
        for (T item : listA) {
            if (!setB.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> List<T> getElementsBefore(List<T> list, Predicate<T> condition) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (condition.test(list.get(i))) {
                index = i;
                break;
            }
        }

        if (index <= 0) {
            return Collections.emptyList();
        }

        return new ArrayList<>(list.subList(0, index));
    }
}
