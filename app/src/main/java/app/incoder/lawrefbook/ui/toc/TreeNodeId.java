package app.incoder.lawrefbook.ui.toc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TreeNodeId
 *
 * @author : Jerry xu
 * @since : 2022/5/3 08:56
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TreeNodeId {
}