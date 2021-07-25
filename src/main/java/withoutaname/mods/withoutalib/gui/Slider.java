package withoutaname.mods.withoutalib.gui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class Slider extends AbstractSliderButton {
	
	private final int min;
	private final int max;
	private final Consumer<Slider> onChange;
	@Nullable
	private final MutableComponent prefix;
	@Nullable
	private final Component suffix;
	
	public Slider(int pX, int pY, int pWidth, int pHeight, int min, int max, int value, Consumer<Slider> onChange) {
		this(pX, pY, pWidth, pHeight, min, max, value, onChange, null, null);
	}
	
	public Slider(int pX, int pY, int pWidth, int pHeight, int min, int max, int value, Consumer<Slider> onChange, Component message) {
		this(pX, pY, pWidth, pHeight, min, max, value, onChange);
		setMessage(message);
	}
	
	public Slider(int pX, int pY, int pWidth, int pHeight, int min, int max, int value, Consumer<Slider> onChange, @Nullable MutableComponent prefix, @Nullable Component suffix) {
		super(pX, pY, pWidth, pHeight, TextComponent.EMPTY, 0);
		this.min = min;
		this.max = max;
		this.onChange = onChange;
		setValue(value);
		this.prefix = prefix;
		this.suffix = suffix;
		updateMessage();
	}
	
	@Override
	protected void updateMessage() {
		if (prefix != null || suffix != null) {
			MutableComponent message = new TextComponent(String.valueOf(getValue()));
			if (prefix != null) {
				message = prefix.append(message);
			}
			if (suffix != null) {
				message.append(suffix);
			}
			setMessage(message);
		}
	}
	
	@Override
	protected void applyValue() {
		onChange.accept(this);
	}
	
	public int getValue() {
		return (int) (value * (max - min) + min);
	}
	
	public void setValue(int value) {
		if (value > max) {
			value = max;
		} else if (value < min) {
			value = min;
		}
		this.value = (value - min) / (double) (max - min);
	}
}
