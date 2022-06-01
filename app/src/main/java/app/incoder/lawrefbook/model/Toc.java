package app.incoder.lawrefbook.model;

import lombok.Data;

/**
 * Toc
 *
 * @author : Jerry xu
 * @since : 2022/5/28 10:20
 */
@Data
public class Toc {
    private String title;
    private int position;
    private int titleLevel;
}
