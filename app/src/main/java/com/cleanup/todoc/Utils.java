package com.cleanup.todoc;

import androidx.annotation.VisibleForTesting;

public abstract class Utils {

    /**
     * List of all possible sort methods for task
     */
    public enum SortMethod {
        /**
         * Sort by date (firstly created first according to SQL request)
         */
        OLD_FIRST,
        /**
         * Sort by date (firstly created first according to SQL request)
         */
        RECENT_FIRST,
        /**
         * Sort by project (projects ordered by id according to SQL request)
         */
        PROJECT_AZ,
        /**
         * No sort
         */
        NONE,

        @VisibleForTesting
        UNEXPECTED_SORTING
    }
}
