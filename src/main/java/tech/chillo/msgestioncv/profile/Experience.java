package tech.chillo.msgestioncv.profile;

import java.util.Set;

public record Experience(
        String startDate,
        String endDate,
        String job,
        String company,
        String location,
        Set<Item> tasks
) {
}
