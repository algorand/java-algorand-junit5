package com.algorand.goran.junit;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(AlgorandTestExtension.class)
public @interface AlgorandPrivateNetwork {
    boolean devMode() default false;
    String binDirOverride() default "";
    String tmpDirOverride() default "";
    String networkTemplateOverride() default "";
}
