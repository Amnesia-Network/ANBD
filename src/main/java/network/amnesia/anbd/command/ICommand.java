package network.amnesia.anbd.command;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ICommand {
    String name();
    CommandCategory category();
    String description();
    Permission[] defaultPermissions() default {};
    boolean guildOnly() default true;
    boolean restricted() default false;
    boolean preview() default false;
}
