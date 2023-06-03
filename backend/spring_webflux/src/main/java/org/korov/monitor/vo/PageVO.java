package org.korov.monitor.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * @author korov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> {
    private int total;
    private int startPage;
    private int pageSize;
    private Collection<T> pageData;
}
