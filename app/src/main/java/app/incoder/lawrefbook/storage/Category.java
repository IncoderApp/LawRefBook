package app.incoder.lawrefbook.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Category
 *
 * @author : Jerry xu
 * @since : 2022/5/3 07:00
 */
@Getter
@AllArgsConstructor
public enum Category {
    /**
     * Category
     */
    FULL_CATEGORY("full"),
    SNIPPETS_CATEGORY("snippets");

    private final String name;
}
