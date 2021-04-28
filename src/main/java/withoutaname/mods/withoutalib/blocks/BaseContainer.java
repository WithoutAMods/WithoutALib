package withoutaname.mods.withoutalib.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public abstract class BaseContainer extends Container {
	
	private final int INVENTORY_SIZE;
	
	public BaseContainer(@Nullable ContainerType<?> type, int id, int containerInventorySize) {
		super(type, id);
		this.INVENTORY_SIZE = containerInventorySize;
	}
	
	/**
	 * Adds a row of slots to the GUI.
	 *
	 * @param handler
	 *        {@link IItemHandler} of the slots
	 * @param index
	 * 		index of the first slot
	 * @param x
	 * 		x-position of the first slot
	 * @param y
	 * 		y-position of the first slot
	 * @param amountX
	 * 		amount of slots which should be added
	 *
	 * @return index of the next slot after this row
	 */
	protected int addSlotRow(IItemHandler handler, int index, int x, int y, int amountX) {
		return addSlotRow(handler, index, x, y, amountX, 18);
	}
	
	/**
	 * Adds a row of slots to the GUI.
	 *
	 * @param handler
	 *        {@link IItemHandler} of the slots
	 * @param index
	 * 		index of the first slot
	 * @param x
	 * 		x-position of the first slot
	 * @param y
	 * 		y-position of the first slot
	 * @param amountX
	 * 		amount of slots which should be added
	 * @param distanceX
	 * 		distance in x-direction between slots
	 *
	 * @return index of the next slot after this row
	 */
	protected int addSlotRow(IItemHandler handler, int index, int x, int y, int amountX, int distanceX) {
		for (int i = 0; i < amountX; i++) {
			addSlot(new SlotItemHandler(handler, index, x, y));
			x += distanceX;
			index++;
		}
		return index;
	}
	
	/**
	 * Adds a box of slots to the GUI.
	 *
	 * @param handler
	 *        {@link IItemHandler} of the slots
	 * @param index
	 * 		index of the first slot
	 * @param x
	 * 		x-position of the first slot
	 * @param y
	 * 		y-position of the first slot
	 * @param amountX
	 * 		amount of slots in one row
	 * @param amountY
	 * 		amount of rows
	 *
	 * @return index of the next slot after this box
	 */
	protected int addSlotBox(IItemHandler handler, int index, int x, int y, int amountX, int amountY) {
		return addSlotBox(handler, index, x, y, amountX, amountY, 18, 18);
	}
	
	/**
	 * Adds a box of slots to the GUI.
	 *
	 * @param handler
	 *        {@link IItemHandler} of the slots
	 * @param index
	 * 		index of the first slot
	 * @param x
	 * 		x-position of the first slot
	 * @param y
	 * 		y-position of the first slot
	 * @param amountX
	 * 		amount of slots in one row
	 * @param amountY
	 * 		amount of rows
	 * @param distanceX
	 * 		distance in x-direction between slots
	 * @param distanceY
	 * 		distance in y-direction between slots
	 *
	 * @return index of the next slot after this box
	 */
	protected int addSlotBox(IItemHandler handler, int index, int x, int y, int amountX, int amountY, int distanceX, int distanceY) {
		for (int i = 0; i < amountY; i++) {
			index = addSlotRow(handler, index, x, y, amountX, distanceX);
			y += distanceY;
		}
		return index;
	}
	
	/**
	 * adds all player inventory slots to the GUI
	 *
	 * @param playerInventory
	 *        {@link IItemHandler} of the inventory
	 * @param x
	 * 		x-position of the first slot
	 * @param y
	 * 		y-position of the first slot
	 */
	protected void addPlayerInventorySlots(IItemHandler playerInventory, int x, int y) {
		// Player inventory
		addSlotBox(playerInventory, 9, x, y, 9, 3);
		
		// Hotbar
		y += 58;
		addSlotRow(playerInventory, 0, x, y, 9);
	}
	
	@Nonnull
	@Override
	public ItemStack quickMoveStack(@Nonnull PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			itemstack = stack.copy();
			if (index < this.INVENTORY_SIZE) {
				if (!this.moveItemStackTo(stack, this.INVENTORY_SIZE, this.INVENTORY_SIZE + 36, false)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(stack, itemstack);
			} else {
				if (!this.moveItemStackTo(stack, 0, this.INVENTORY_SIZE, false)) {
					if (index < this.INVENTORY_SIZE + 27) {
						if (!this.moveItemStackTo(stack, this.INVENTORY_SIZE + 27, this.INVENTORY_SIZE + 36, false)) {
							return ItemStack.EMPTY;
						}
					} else if (index < this.INVENTORY_SIZE + 36 && !this.moveItemStackTo(stack, this.INVENTORY_SIZE, this.INVENTORY_SIZE + 27, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
			
			if (stack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			
			if (stack.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			
			slot.onTake(playerIn, stack);
		}
		
		return itemstack;
	}
	
}
