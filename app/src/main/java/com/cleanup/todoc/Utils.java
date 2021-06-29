package com.cleanup.todoc;

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
        PROJECT_ID_ORDER,
        /**
         * No sort
         */
        NONE
    }
}
