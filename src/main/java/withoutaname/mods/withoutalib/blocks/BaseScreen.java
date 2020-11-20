package withoutaname.mods.withoutalib.blocks;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BaseScreen<T extends Container> extends ContainerScreen<T> {

   private final ResourceLocation GUI_TEXTURE;

   public BaseScreen(T container, ResourceLocation gui_texture, PlayerInventory playerInventory, ITextComponent title, int xSize, int ySize) {
      super(container, playerInventory, title);
      this.GUI_TEXTURE = gui_texture;
      this.xSize = xSize;
      this.ySize = ySize;
   }

   @Override
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.renderBackground();
      super.render(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int x, int y) {
      this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
      this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 94), 4210752);
   }

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.blit(i, j, 0, 0, this.xSize, this.ySize);
   }
}