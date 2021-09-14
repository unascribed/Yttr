package com.unascribed.yttr.network.concrete.annotation.type;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that a Message may be processed for spectating players. Normally, a Message sent by a
 * spectator will be ignored.
 * <p>
 * <i>No effect if the Message is {@link ReceivedOn @ReceivedOn(EnvType.CLIENT)}.</i>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidForSpectators {

}
