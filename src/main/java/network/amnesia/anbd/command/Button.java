package network.amnesia.anbd.command;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import net.dv8tion.jda.internal.utils.Checks;
import network.amnesia.anbd.Main;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.UUID;

public class Button extends ButtonImpl {
    public static Button primary(String label) {
        return primary(UUID.randomUUID().toString(), label);
    }

    public static Button primary(Emoji emoji) {
        return primary(UUID.randomUUID().toString(), emoji);
    }
    public static Button success(String label) {
        return success(UUID.randomUUID().toString(), label);
    }

    public static Button success(Emoji emoji) {
        return success(UUID.randomUUID().toString(), emoji);
    }
    
    public static Button danger(String label) {
        return danger(UUID.randomUUID().toString(), label);
    }

    public static Button danger(Emoji emoji) {
        return danger(UUID.randomUUID().toString(), emoji);
    }

    public static Button link(String label) {
        return link(UUID.randomUUID().toString(), label);
    }

    public static Button link(Emoji emoji) {
        return link(UUID.randomUUID().toString(), emoji);
    }

    @Nonnull
    static Button primary(@Nonnull String id, @Nonnull String label)
    {
        Checks.notEmpty(id, "Id");
        Checks.notEmpty(label, "Label");
        Checks.notLonger(id, ID_MAX_LENGTH, "Id");
        Checks.notLonger(label, LABEL_MAX_LENGTH, "Label");
        return new Button(id, label, ButtonStyle.PRIMARY, false, null);
    }
    
    @Nonnull
    static Button primary(@Nonnull String id, @Nonnull Emoji emoji)
    {
        Checks.notEmpty(id, "Id");
        Checks.notNull(emoji, "Emoji");
        Checks.notLonger(id, ID_MAX_LENGTH, "Id");
        return new Button(id, "", ButtonStyle.PRIMARY, false, emoji);
    }

    
    @Nonnull
    static Button secondary(@Nonnull String id, @Nonnull String label)
    {
        Checks.notEmpty(id, "Id");
        Checks.notEmpty(label, "Label");
        Checks.notLonger(id, ID_MAX_LENGTH, "Id");
        Checks.notLonger(label, LABEL_MAX_LENGTH, "Label");
        return new Button(id, label, ButtonStyle.SECONDARY, false, null);
    }

    @Nonnull
    static Button secondary(@Nonnull String id, @Nonnull Emoji emoji)
    {
        Checks.notEmpty(id, "Id");
        Checks.notNull(emoji, "Emoji");
        Checks.notLonger(id, ID_MAX_LENGTH, "Id");
        return new Button(id, "", ButtonStyle.SECONDARY, false, emoji);
    }

    @Nonnull
    static Button success(@Nonnull String id, @Nonnull String label)
    {
        Checks.notEmpty(id, "Id");
        Checks.notEmpty(label, "Label");
        Checks.notLonger(id, ID_MAX_LENGTH, "Id");
        Checks.notLonger(label, LABEL_MAX_LENGTH, "Label");
        return new Button(id, label, ButtonStyle.SUCCESS, false, null);
    }

    @Nonnull
    static Button success(@Nonnull String id, @Nonnull Emoji emoji)
    {
        Checks.notEmpty(id, "Id");
        Checks.notNull(emoji, "Emoji");
        Checks.notLonger(id, ID_MAX_LENGTH, "Id");
        return new Button(id, "", ButtonStyle.SUCCESS, false, emoji);
    }

    @Nonnull
    static Button danger(@Nonnull String id, @Nonnull String label)
    {
        Checks.notEmpty(id, "Id");
        Checks.notEmpty(label, "Label");
        Checks.notLonger(id, ID_MAX_LENGTH, "Id");
        Checks.notLonger(label, LABEL_MAX_LENGTH, "Label");
        return new Button(id, label, ButtonStyle.DANGER, false, null);
    }

    @Nonnull
    static Button danger(@Nonnull String id, @Nonnull Emoji emoji)
    {
        Checks.notEmpty(id, "Id");
        Checks.notNull(emoji, "Emoji");
        Checks.notLonger(id, ID_MAX_LENGTH, "Id");
        return new Button(id, "", ButtonStyle.DANGER, false, emoji);
    }

    @Nonnull
    static Button link(@Nonnull String url, @Nonnull String label)
    {
        Checks.notEmpty(url, "URL");
        Checks.notEmpty(label, "Label");
        Checks.notLonger(url, URL_MAX_LENGTH, "URL");
        Checks.notLonger(label, LABEL_MAX_LENGTH, "Label");
        return new Button(null, label, ButtonStyle.LINK, url, false, null);
    }

    @Nonnull
    static Button link(@Nonnull String url, @Nonnull Emoji emoji)
    {
        Checks.notEmpty(url, "URL");
        Checks.notNull(emoji, "Emoji");
        Checks.notLonger(url, URL_MAX_LENGTH, "URL");
        return new Button(null, "", ButtonStyle.LINK, url, false, emoji);
    }

    @Nonnull
    static Button of(@Nonnull ButtonStyle style, @Nonnull String idOrUrl, @Nonnull String label)
    {
        Checks.check(style != ButtonStyle.UNKNOWN, "Cannot make button with unknown style!");
        Checks.notNull(style, "Style");
        Checks.notNull(label, "Label");
        Checks.notLonger(label, LABEL_MAX_LENGTH, "Label");
        if (style == ButtonStyle.LINK)
            return link(idOrUrl, label);
        Checks.notEmpty(idOrUrl, "Id");
        Checks.notLonger(idOrUrl, ID_MAX_LENGTH, "Id");
        return new Button(idOrUrl, label, style, false, null);
    }

    @Nonnull
    static Button of(@Nonnull ButtonStyle style, @Nonnull String idOrUrl, @Nonnull Emoji emoji)
    {
        Checks.check(style != ButtonStyle.UNKNOWN, "Cannot make button with unknown style!");
        Checks.notNull(style, "Style");
        Checks.notNull(emoji, "Emoji");
        if (style == ButtonStyle.LINK)
            return link(idOrUrl, emoji);
        Checks.notEmpty(idOrUrl, "Id");
        Checks.notLonger(idOrUrl, ID_MAX_LENGTH, "Id");
        return new Button(idOrUrl, "", style, false, emoji);
    }

    @Nonnull
    static Button of(@Nonnull ButtonStyle style, @Nonnull String idOrUrl, @javax.annotation.Nullable String label, @javax.annotation.Nullable Emoji emoji)
    {
        if (label != null)
            return of(style, idOrUrl, label).withEmoji(emoji);
        else if (emoji != null)
            return of(style, idOrUrl, emoji);
        throw new IllegalArgumentException("Cannot build a button without a label and emoji. At least one has to be provided as non-null.");
    }

    public Button(DataObject data) {
        super(data);
    }

    public Button(String id, String label, ButtonStyle style, boolean disabled, Emoji emoji) {
        super(id, label, style, disabled, emoji);
    }

    public Button(String id, String label, ButtonStyle style, String url, boolean disabled, Emoji emoji) {
        super(id, label, style, url, disabled, emoji);
    }

    @NotNull
    @Override
    public Button asDisabled() {
        return withDisabled(true);
    }

    @NotNull
    @Override
    public Button asEnabled() {
        return withDisabled(false);
    }

    @NotNull
    @Override
    public Button withDisabled(boolean disabled) {
        return new Button(getId(), getLabel(), getStyle(), getUrl(), disabled, getEmoji());
    }

    @NotNull
    @Override
    public Button withEmoji(@Nullable Emoji emoji) {
        return new Button(getId(), getLabel(), getStyle(), getUrl(), isDisabled(), emoji);
    }

    @NotNull
    @Override
    public Button withLabel(@NotNull String label) {
        Checks.notEmpty(label, "Label");
        Checks.notLonger(label, LABEL_MAX_LENGTH, "Label");
        return new Button(getId(), label, getStyle(), getUrl(), isDisabled(), getEmoji());
    }

    @NotNull
    @Override
    public Button withId(@NotNull String id) {
        Checks.notEmpty(id, "ID");
        Checks.notLonger(id, ID_MAX_LENGTH, "ID");
        return new Button(id, getLabel(), getStyle(), null, isDisabled(), getEmoji());
    }

    @NotNull
    @Override
    public Button withUrl(@NotNull String url) {
        Checks.notEmpty(url, "URL");
        Checks.notLonger(url, URL_MAX_LENGTH, "URL");
        return new Button(null, getLabel(), ButtonStyle.LINK, url, isDisabled(), getEmoji());
    }

    @NotNull
    @Override
    public Button withStyle(@NotNull ButtonStyle style) {
        Checks.notNull(style, "Style");
        Checks.check(style != ButtonStyle.UNKNOWN, "Cannot make button with unknown style!");
        if (getStyle() == ButtonStyle.LINK && style != ButtonStyle.LINK)
            throw new IllegalArgumentException("You cannot change a link button to another style!");
        if (getStyle() != ButtonStyle.LINK && style == ButtonStyle.LINK)
            throw new IllegalArgumentException("You cannot change a styled button to a link button!");
        return new Button(getId(), getLabel(), style, getUrl(), isDisabled(), getEmoji());
    }
    
    public Button onClick(ButtonCallback callback) {
        Main.getButtonManager().registerCallback(getId(), callback);
        return this;
    }
}
