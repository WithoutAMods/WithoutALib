package withoutaname.mods.withoutalib.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public abstract class BaseContainer extends Container {

	private final int INVENTORY_SIZE;

	public BaseContainer(@Nullable ContainerType<?> type, int id, int containerInventorySize) {
		super(type, id);
		this.INVENTORY_SIZE = containerInventorySize;
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

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			itemstack = stack.copy();
			if (index < this.INVENTORY_SIZE) {
				if (!this.mergeItemStack(stack, this.INVENTORY_SIZE, this.INVENTORY_SIZE + 36, false)) {
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(stack, itemstack);
			} else {
				if (!this.mergeItemStack(stack, 0, this.INVENTORY_SIZE, false)) {
					if (index < this.INVENTORY_SIZE + 27) {
						if (!this.mergeItemStack(stack, this.INVENTORY_SIZE + 27, this.INVENTORY_SIZE + 36, false)) {
							return ItemStack.EMPTY;
						}
					} else if (index < this.INVENTORY_SIZE + 36 && !this.mergeItemStack(stack, this.INVENTORY_SIZE, this.INVENTORY_SIZE + 27, false)) {
						return ItemStack.EMPTY;
					}
				}
			}

			if (stack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (stack.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, stack);
		}

		return itemstack;
	}

}
