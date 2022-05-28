package me.ichun.mods.ichunutil.common.module.tabula;

import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButtonTextured;
import me.ichun.mods.ichunutil.common.module.tabula.project.Project;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Written for Hats to add in buttons for Tabula. Kept in iChunUtil because I didn't want to add dependency on Tabula
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public abstract class TabulaPlugin
{
    /**
     * An instance of the Tabula Workspace (WorkspaceTabula, which implements TabulaWorkspace)
     */
    public TabulaWorkspace tabulaWorkspace;

    /**
     * Called when WorkspaceTabula calls init();
     * @param workspace WorkspaceTabula instance
     */
    public void onInit(@Nonnull TabulaWorkspace workspace)
    {
        this.tabulaWorkspace = workspace;
    }

    /**
     * Called when WorkspaceTabula calls onClose();
     * @param workspace WorkspaceTabula instance
     */
    public void onClose(@Nonnull TabulaWorkspace workspace)
    {
        this.tabulaWorkspace = null;
    }

    /**
     * Called when ViewToolbar (in Tabula) calls populate(ProjectInfo info)
     * @param workspace WorkspaceTabula instance
     * @param toolbarView ViewToolbar instance
     * @param currentProject The currently opened project. Can be null.
     */
    public ElementButtonTextured<?> onPopulateToolbar(@Nonnull TabulaWorkspace workspace, @Nonnull View<?> toolbarView, @Nonnull ElementButtonTextured<?> lastToolbarButton, @Nullable Project currentProject)
    {
        return lastToolbarButton;
    }

    public interface TabulaWorkspace
    {
        void openProject(@Nonnull Project project, @Nullable Project ghostProject, float ghostOpacity);
    }
}
