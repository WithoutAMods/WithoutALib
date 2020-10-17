package withoutaname.mods.withoutalib.blocks;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class BaseContainer extends Container {

	protected BaseContainer(@Nullable ContainerType<?> type, int id) {
		super(type, id);
	}

	protected int addSlotRow(IItemHandler handler, int index, int x, int y, int amountX) {
		return addSlotRow(handler, index, x, y, amountX, 18);
	}

	protected int addSlotRow(IItemHandler handler, int index, int x, int y, int amountX, int distanceX) {
		for(int i = 0; i < amountX; i++) {
			addSlot(new SlotItemHandler(handler, index, x, y));
			x += distanceX;
			index++;
		}
		return index;
	}

	protected int addSlotBox(IItemHandler handler, int index, int x, int y, int amountX, int amountY) {
		return addSlotBox(handler, index, x, y, amountX, amountY, 18, 18);
	}

	protected int addSlotBox(IItemHandler handler, int index, int x, int y, int amountX, int amountY, int distanceX, int distanceY) {
		for(int i = 0; i < amountY; i++) {
			index = addSlotRow(handler, index, x, y, amountX, distanceX);
			y += distanceY;
		}
		return index;
	}

	protected void addPlayerInventorySlots(IItemHandler playerInventory, int x, int y) {
		// Player inventory
		addSlotBox(playerInventory, 9, x, y, 9, 3);

		// Hotbar
		y += 58;
		addSlotRow(playerInventory, 0, x, y, 9);
	}

}
