package me.ichun.mods.ichunutil.api.worldportals;

public class WorldPortalsApi
{
    private static IApi apiImpl = new ApiDummy();

    /**
     * Get the IApi implementation for World Portals.
     *
     * @return returns the IApi implementation from world portals. May be the ApiDummy if World Portals has not loaded.
     */
    public static IApi getApiImpl()
    {
        return apiImpl;
    }

    /**
     * Sets the IApi implementation for World Portals.
     * For use of World Portals, so please don't actually use this.
     *
     * @param apiImpl API implementation to set.
     */
    public static void setApiImpl(IApi apiImpl)
    {
        WorldPortalsApi.apiImpl = apiImpl;
    }
}
