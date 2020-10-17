package withoutaname.mods.withoutalib.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HopperContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BaseScreen<T extends Container> extends ContainerScreen<T> {

   private final ResourceLocation GUI_TEXTURE;

   public BaseScreen(T container, ResourceLocation gui_texture, PlayerInventory playerInventory, ITextComponent title) {
      super(container, playerInventory, title);
      this.GUI_TEXTURE = gui_texture;
   }

   @Override
   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack);
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
   }

   @Override
   protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
   }
}