package com.hakan.core.item;

import com.hakan.core.HCore;
import com.hakan.core.item.nbt.HNbtManager;
import com.hakan.core.utils.ColorUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * HItemBuilder class to create and
 * manage item stacks easily.
 */
public class HItemBuilder {

    private static Enchantment glowEnchantment;
    private static HNbtManager nbtManager;

    /**
     * initialize method of HItemStack class.
     */
    public static void initialize() {
        try {
            Constructor<?> cons = Class.forName("com.hakan.core.item.nbt.HNbtManager_" + HCore.getVersionString())
                    .getDeclaredConstructor();
            cons.setAccessible(true);
            HItemBuilder.nbtManager = (HNbtManager) cons.newInstance();
            cons.setAccessible(false);

            if (HItemBuilder.glowEnchantment == null) {
                Constructor<?> cons2 = Class.forName("com.hakan.core.item.enchantment.EnchantmentGlow_" + HCore.getVersionString())
                        .getDeclaredConstructor(int.class);
                cons2.setAccessible(true);
                HItemBuilder.glowEnchantment = (Enchantment) cons2.newInstance(152634);
                cons2.setAccessible(false);

                if (Arrays.asList(Enchantment.values()).contains(glowEnchantment))
                    return;

                Field field = Enchantment.class.getDeclaredField("acceptingNew");
                field.setAccessible(true);
                field.setBoolean(HItemBuilder.glowEnchantment, true);
                Enchantment.registerEnchantment(HItemBuilder.glowEnchantment);
                field.setAccessible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets Glow enchantment.
     *
     * @return Glow enchantment.
     */
    @Nonnull
    public static Enchantment getGlowEnchantment() {
        return HItemBuilder.glowEnchantment;
    }

    /**
     * Gets NbtManager object.
     *
     * @return NbtManager object.
     */
    @Nonnull
    public static HNbtManager getNbtManager() {
        return HItemBuilder.nbtManager;
    }


    private Material type;
    private String nbt;
    private String name;
    private int amount;
    private short durability;
    private boolean glow;
    private List<String> lore;
    private Set<ItemFlag> flags;
    private Map<Enchantment, Integer> enchantments;

    /**
     * Creates new instance of this class.
     *
     * @param type Material type.
     */
    public HItemBuilder(@Nonnull Material type) {
        this(type, 1);
    }

    /**
     * Creates new instance of this class.
     *
     * @param type   Material type.
     * @param amount Amount.
     */
    public HItemBuilder(@Nonnull Material type, int amount) {
        this(type, amount, (short) 0);
    }

    /**
     * Creates new instance of this class.
     *
     * @param type       Material type.
     * @param amount     Amount.
     * @param durability Datavalue.
     */
    public HItemBuilder(@Nonnull Material type, int amount, short durability) {
        this.type = type;
        this.nbt = "{}";
        this.name = "";
        this.amount = amount;
        this.durability = durability;
        this.glow = false;
        this.lore = new ArrayList<>();
        this.flags = new HashSet<>();
        this.enchantments = new HashMap<>();
    }

    /**
     * Creates new instance of this class from item builder.
     *
     * @param builder Item builder.
     */
    public HItemBuilder(@Nonnull HItemBuilder builder) {
        this.type = builder.type;
        this.nbt = builder.nbt;
        this.name = builder.name;
        this.amount = builder.amount;
        this.durability = builder.durability;
        this.glow = builder.glow;
        this.lore = new ArrayList<>(builder.lore);
        this.flags = new HashSet<>(builder.flags);
        this.enchantments = new HashMap<>(builder.enchantments);
    }

    /**
     * Creates new instance of this class from item stack.
     *
     * @param stack Item stack.
     */
    public HItemBuilder(@Nonnull ItemStack stack) {
        this(stack.getType(), stack.getAmount(), stack.getDurability());
        this.nbt = HItemBuilder.nbtManager.get(stack);

        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            this.name = meta.getDisplayName();
            this.flags = meta.getItemFlags();
            this.lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            this.enchantments = meta.hasEnchants() ? meta.getEnchants() : new HashMap<>();
            this.glow = meta.hasEnchants() && meta.getEnchants().containsKey(glowEnchantment);
        }
    }

    /**
     * Gets type of item stack.
     *
     * @return Type of item stack.
     */
    @Nonnull
    public Material getType() {
        return this.type;
    }

    /**
     * Sets type of item stack.
     *
     * @param type Material type.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder type(@Nonnull Material type) {
        this.type = Objects.requireNonNull(type, "type cannot be null!");
        return this;
    }


    /**
     * Gets name of item stack.
     *
     * @return Name of item stack.
     */
    @Nonnull
    public String getName() {
        return this.name;
    }

    /**
     * Sets name of item stack.
     *
     * @param name Name of item stack.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder name(@Nonnull String name) {
        return this.name(true, name);
    }

    /**
     * Sets name of item stack.
     *
     * @param colored Name is colored.
     * @param name    Name of item stack.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder name(boolean colored, @Nonnull String name) {
        this.name = Objects.requireNonNull(name, "name cannot be null!");
        if (colored) this.name = ColorUtil.colored(this.name);
        return this;
    }


    /**
     * Gets lore of item stack.
     *
     * @return Lore of item stack.
     */
    @Nonnull
    public List<String> getLore() {
        return this.lore;
    }

    /**
     * Sets lore of item stack.
     *
     * @param lore Lore of item stack.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder lores(@Nonnull List<String> lore) {
        return this.lores(true, lore);
    }

    /**
     * Adds lore to item stack.
     *
     * @param lines Lines to add.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder appendLore(@Nonnull String... lines) {
        return this.appendLore(true, lines);
    }

    /**
     * Adds lore to item stack.
     *
     * @param lines Lines to add.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder appendLore(@Nonnull List<String> lines) {
        return this.appendLore(true, lines);
    }

    /**
     * Sets lore of item stack.
     *
     * @param colored Lore is colored.
     * @param lore    Lore of item stack
     * @return This class.
     */
    @Nonnull
    public HItemBuilder lores(boolean colored, @Nonnull List<String> lore) {
        this.lore.clear();
        return this.appendLore(colored, lore);
    }

    /**
     * Adds lore to item stack.
     *
     * @param colored Lore is colored.
     * @param lines   Lines to add.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder appendLore(boolean colored, @Nonnull String... lines) {
        return this.appendLore(colored, Arrays.asList(lines));
    }

    /**
     * Adds lore to item stack.
     *
     * @param colored Lore is colored.
     * @param lines   Lines to add.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder appendLore(boolean colored, @Nonnull List<String> lines) {
        for (String _line : Objects.requireNonNull(lines, "lines cannot be null!")) {
            String line = Objects.requireNonNull(_line, "lore cannot be null!");
            if (colored) line = ColorUtil.colored(line);
            this.lore.add(line);
        }

        return this;
    }

    /**
     * Gets amount of item stack.
     *
     * @return Amount of item stack.
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Sets amount of item stack.
     *
     * @param amount Amount of item stack.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }


    /**
     * Gets durability of item stack.
     *
     * @return Durability of item stack.
     */
    public short getDurability() {
        return this.durability;
    }

    /**
     * Sets durability of item stack.
     *
     * @param durability Durability of item stack.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder durability(short durability) {
        this.durability = durability;
        return this;
    }

    /**
     * Checks item stack has any enchantment.
     *
     * @return If item stack has enchantment, returns true.
     */
    public boolean hasEnchants() {
        return this.enchantments.size() > 0;
    }

    /**
     * Checks item stack has enchantment.
     *
     * @param enchantment Enchantment.
     * @return This class.
     */
    public boolean hasEnchant(@Nonnull Enchantment enchantment) {
        return this.enchantments.containsKey(Objects.requireNonNull(enchantment, "enchantment cannot be null!"));
    }

    /**
     * Gets enchantment list.
     *
     * @return Enchantments.
     */
    @Nonnull
    public Map<Enchantment, Integer> getEnchants() {
        return this.enchantments;
    }

    /**
     * Gets level from enchantment.
     *
     * @param enchantment Enchantment.
     * @return Level of enchantment.
     */
    public int getEnchantLevel(@Nonnull Enchantment enchantment) {
        return this.enchantments.get(Objects.requireNonNull(enchantment, "enchantment cannot be null!"));
    }

    /**
     * Adds enchantment to item stack.
     *
     * @param enchantment Enchantment.
     * @param level       Level.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder addEnchant(@Nonnull Enchantment enchantment, int level) {
        this.enchantments.put(Objects.requireNonNull(enchantment, "enchantment cannot be null!"), level);
        return this;
    }

    /**
     * Removes enchantment from item stack.
     *
     * @param enchantment Enchantment.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder removeEnchant(@Nonnull Enchantment enchantment) {
        this.enchantments.remove(Objects.requireNonNull(enchantment, "enchantment cannot be null!"));
        return this;
    }


    /**
     * Checks item stack has flag.
     *
     * @param flag Item flag.
     * @return If item stack has flag, returns true.
     */
    public boolean hasItemFlag(@Nonnull ItemFlag flag) {
        return this.flags.contains(Objects.requireNonNull(flag, "item flag cannot be null!"));
    }

    /**
     * Gets item flag list.
     *
     * @return Item flag list.
     */
    @Nonnull
    public Set<ItemFlag> getItemFlags() {
        return this.flags;
    }

    /**
     * Adds item flags to item stack.
     *
     * @param flags Item flags.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder addItemFlags(@Nonnull ItemFlag... flags) {
        for (ItemFlag flag : Objects.requireNonNull(flags, "item flags cannot be null!"))
            this.flags.add(Objects.requireNonNull(flag, "item flag cannot be null!"));
        return this;
    }

    /**
     * Removes item flags from item stack.
     *
     * @param flags Item flags.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder removeItemFlags(@Nonnull ItemFlag... flags) {
        for (ItemFlag flag : Objects.requireNonNull(flags, "item flags cannot be null!"))
            this.flags.remove(Objects.requireNonNull(flag, "item flag cannot be null!"));
        return this;
    }

    /**
     * Gets glow of item stack.
     *
     * @return Returns true, if item is glowing.
     */
    public boolean isGlow() {
        return this.glow;
    }

    /**
     * Sets glow of item stack.
     *
     * @param glow Glow.
     * @return This class.
     */
    @Nonnull
    public HItemBuilder glow(boolean glow) {
        this.glow = glow;
        return this;
    }

    /**
     * Gets nbt tag of item stack.
     *
     * @return Nbt tag.
     */
    public String getNbt() {
        return this.nbt;
    }

    /**
     * Sets nbt of item stack.
     *
     * @param nbt NBT.
     */
    @Nonnull
    public HItemBuilder nbt(@Nonnull String nbt) {
        this.nbt = Objects.requireNonNull(nbt, "nbt cannot be null!");
        return this;
    }

    /**
     * Checks item stack has item meta.
     *
     * @return If item stack has item meta, returns true.
     */
    public boolean hasItemMeta() {
        return true;
    }

    /**
     * Builds item stack.
     *
     * @return Item stack.
     */
    @Nonnull
    public ItemStack build() {
        ItemStack stack = new ItemStack(this.type, this.amount, this.durability);
        stack = nbtManager.set(stack, this.nbt);

        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(this.name);
            meta.setLore(this.lore);
            this.flags.forEach(meta::addItemFlags);
            this.enchantments.forEach((key, value) -> meta.addEnchant(key, value, true));
            if (this.glow) meta.addEnchant(glowEnchantment, 0, true);
            stack.setItemMeta(meta);
        }

        return stack;
    }
}