package hauveli.fishcasting.features.trader

import net.minecraft.client.animation.AnimationChannel
import net.minecraft.client.animation.AnimationDefinition
import net.minecraft.client.animation.Keyframe
import net.minecraft.client.animation.KeyframeAnimations


object BlessedAnimations {
    val casting: AnimationDefinition = AnimationDefinition.Builder.withLength(1.0f).looping()
        .addAnimation(
            "right_arm", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(
                    0.0f,
                    KeyframeAnimations.degreeVec(-70.2964f, -34.9023f, 0.8793f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.25f,
                    KeyframeAnimations.degreeVec(-93.7013f, -30.9182f, 13.7291f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.5f,
                    KeyframeAnimations.degreeVec(-87.5951f, -11.7324f, 11.4751f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.75f,
                    KeyframeAnimations.degreeVec(-67.5111f, -16.2859f, 6.5621f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.0f,
                    KeyframeAnimations.degreeVec(-70.2964f, -34.9023f, 0.8793f),
                    AnimationChannel.Interpolations.CATMULLROM
                )
            )
        )
        .addAnimation(
            "right_finger_front", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(
                    0.0f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 45.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.2917f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 22.5f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.5833f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.8333f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 4.5f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.0f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 45.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                )
            )
        )
        .addAnimation(
            "right_finger_back", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(
                    0.0f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 22.5f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.2917f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 45.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.5833f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 22.5f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.8333f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.0f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 22.5f),
                    AnimationChannel.Interpolations.CATMULLROM
                )
            )
        )
        .addAnimation(
            "right_thumb", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(
                    0.0f,
                    KeyframeAnimations.degreeVec(-67.5f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.2917f,
                    KeyframeAnimations.degreeVec(-45.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.5833f,
                    KeyframeAnimations.degreeVec(-22.5f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.8333f,
                    KeyframeAnimations.degreeVec(-22.5f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.0f,
                    KeyframeAnimations.degreeVec(-67.5f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                )
            )
        )
        .build()

    val begin_cast: AnimationDefinition = AnimationDefinition.Builder.withLength(0.25f)
        .addAnimation(
            "right_arm", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(
                    0.25f,
                    KeyframeAnimations.degreeVec(-70.2964f, -34.9023f, 0.8793f),
                    AnimationChannel.Interpolations.LINEAR
                )
            )
        )
        .addAnimation(
            "right_finger_front", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.25f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 45.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "right_finger_back", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.25f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 22.5f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "right_thumb", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(
                    0.25f,
                    KeyframeAnimations.degreeVec(-67.5f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                )
            )
        )
        .build()

    val blank: AnimationDefinition = AnimationDefinition.Builder.withLength(0.25f)
        .addAnimation(
            "right_arm", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .build()

    val panic: AnimationDefinition = AnimationDefinition.Builder.withLength(0.0f)

        .build()

    val walk: AnimationDefinition = AnimationDefinition.Builder.withLength(0.5f).looping()
        .addAnimation(
            "left_arm", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(
                    0.125f,
                    KeyframeAnimations.degreeVec(-5.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                ),
                Keyframe(0.25f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(
                    0.375f,
                    KeyframeAnimations.degreeVec(5.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                ),
                Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "left_leg", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(
                    0.0f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.125f,
                    KeyframeAnimations.degreeVec(30.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.25f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.375f,
                    KeyframeAnimations.degreeVec(-30.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.5f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                )
            )
        )
        .addAnimation(
            "right_leg", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(
                    0.0f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.125f,
                    KeyframeAnimations.degreeVec(-30.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.25f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.375f,
                    KeyframeAnimations.degreeVec(30.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.5f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                )
            )
        )
        .addAnimation(
            "right_arm", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(
                    0.125f,
                    KeyframeAnimations.degreeVec(5.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                ),
                Keyframe(0.25f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(
                    0.375f,
                    KeyframeAnimations.degreeVec(-5.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                ),
                Keyframe(0.5f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .build()

    val standIdle: AnimationDefinition = AnimationDefinition.Builder.withLength(1.0f)
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "left_arm", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, -4.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "left_arm", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "left_leg", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(
                    0.0f,
                    KeyframeAnimations.degreeVec(-0.2795f, 3.9903f, -4.0098f),
                    AnimationChannel.Interpolations.LINEAR
                )
            )
        )
        .addAnimation(
            "right_leg", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(
                    0.0f,
                    KeyframeAnimations.degreeVec(-0.2795f, -3.9904f, 4.0097f),
                    AnimationChannel.Interpolations.LINEAR
                )
            )
        )
        .addAnimation(
            "right_arm", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 4.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "right_arm", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "simplify_logic", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "simplify_logic2", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "simplify_logic2", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .build()

    val breathe: AnimationDefinition = AnimationDefinition.Builder.withLength(2.0f).looping()
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM),
                Keyframe(
                    0.25f,
                    KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.75f,
                    KeyframeAnimations.posVec(0.0f, -0.2f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.0f,
                    KeyframeAnimations.posVec(0.0f, -0.2f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.25f,
                    KeyframeAnimations.posVec(0.0f, -0.2f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.75f,
                    KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(2.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "body", AnimationChannel(
                AnimationChannel.Targets.SCALE,
                Keyframe(0.0f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.25f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.75f, KeyframeAnimations.scaleVec(1.0, 0.96, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.0f, KeyframeAnimations.scaleVec(1.0, 0.96, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.25f, KeyframeAnimations.scaleVec(1.0, 0.96, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.75f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(2.0f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "left_arm", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM),
                Keyframe(
                    0.25f,
                    KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.75f,
                    KeyframeAnimations.posVec(0.0f, -0.2f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.0f,
                    KeyframeAnimations.posVec(0.0f, -0.2f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.25f,
                    KeyframeAnimations.posVec(0.0f, -0.2f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.75f,
                    KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(2.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "left_arm", AnimationChannel(
                AnimationChannel.Targets.SCALE,
                Keyframe(0.0f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.25f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.75f, KeyframeAnimations.scaleVec(1.0, 0.98, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.0f, KeyframeAnimations.scaleVec(1.0, 0.98, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.25f, KeyframeAnimations.scaleVec(1.0, 0.98, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.75f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(2.0f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "left_leg", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.25f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.75f, KeyframeAnimations.posVec(0.0f, -0.1f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.0f, KeyframeAnimations.posVec(0.0f, -0.1f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.25f, KeyframeAnimations.posVec(0.0f, -0.1f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.75f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(2.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "left_leg", AnimationChannel(
                AnimationChannel.Targets.SCALE,
                Keyframe(0.0f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.25f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.75f, KeyframeAnimations.scaleVec(1.0, 0.98, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.0f, KeyframeAnimations.scaleVec(1.0, 0.98, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.25f, KeyframeAnimations.scaleVec(1.0, 0.98, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.75f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(2.0f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "tail", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.25f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.75f, KeyframeAnimations.posVec(0.0f, -0.2f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.0f, KeyframeAnimations.posVec(0.0f, -0.2f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.25f, KeyframeAnimations.posVec(0.0f, -0.2f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.75f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(2.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "right_leg", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.25f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.75f, KeyframeAnimations.posVec(0.0f, -0.1f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.0f, KeyframeAnimations.posVec(0.0f, -0.1f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.25f, KeyframeAnimations.posVec(0.0f, -0.1f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.75f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(2.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "right_leg", AnimationChannel(
                AnimationChannel.Targets.SCALE,
                Keyframe(0.0f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.25f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.75f, KeyframeAnimations.scaleVec(1.0, 0.98, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.0f, KeyframeAnimations.scaleVec(1.0, 0.98, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.25f, KeyframeAnimations.scaleVec(1.0, 0.98, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.75f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(2.0f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "right_arm", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(
                    0.0f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.25f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.75f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                )
            )
        )
        .addAnimation(
            "right_arm", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM),
                Keyframe(
                    0.25f,
                    KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.75f,
                    KeyframeAnimations.posVec(0.0f, -0.2f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.0f,
                    KeyframeAnimations.posVec(0.0f, -0.2f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.25f,
                    KeyframeAnimations.posVec(0.0f, -0.2f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.75f,
                    KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(2.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "right_arm", AnimationChannel(
                AnimationChannel.Targets.SCALE,
                Keyframe(0.0f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.25f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.75f, KeyframeAnimations.scaleVec(1.0, 0.98, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.0f, KeyframeAnimations.scaleVec(1.0, 0.98, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.25f, KeyframeAnimations.scaleVec(1.0, 0.98, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.75f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR),
                Keyframe(2.0f, KeyframeAnimations.scaleVec(1.0, 1.0, 1.0), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "simplify_logic", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM),
                Keyframe(
                    1.0f,
                    KeyframeAnimations.posVec(0.0f, -0.3f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(2.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "simplify_logic2", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM),
                Keyframe(
                    0.25f,
                    KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    0.75f,
                    KeyframeAnimations.posVec(0.0f, -0.3f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.0f,
                    KeyframeAnimations.posVec(0.0f, -0.3f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.25f,
                    KeyframeAnimations.posVec(0.0f, -0.3f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(
                    1.75f,
                    KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.CATMULLROM
                ),
                Keyframe(2.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.CATMULLROM)
            )
        )
        .addAnimation(
            "glasses", AnimationChannel(
                AnimationChannel.Targets.POSITION,
                Keyframe(0.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.25f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(0.75f, KeyframeAnimations.posVec(0.0f, -0.4f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.0f, KeyframeAnimations.posVec(0.0f, -0.4f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.25f, KeyframeAnimations.posVec(0.0f, -0.4f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(1.75f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(2.0f, KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .build()

    val ear_twitch_left: AnimationDefinition = AnimationDefinition.Builder.withLength(0.1667f)
        .addAnimation(
            "leftEar", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(
                    0.0833f,
                    KeyframeAnimations.degreeVec(12.5f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                ),
                Keyframe(
                    0.1667f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                )
            )
        )
        .build()

    val ear_twitch_right: AnimationDefinition = AnimationDefinition.Builder.withLength(0.1667f)
        .addAnimation(
            "rightEar", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(
                    0.0833f,
                    KeyframeAnimations.degreeVec(-12.5f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                ),
                Keyframe(
                    0.1667f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                )
            )
        )
        .build()

    val tail_wiggle: AnimationDefinition = AnimationDefinition.Builder.withLength(0.25f).looping()
        .addAnimation(
            "tail", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR),
                Keyframe(
                    0.0417f,
                    KeyframeAnimations.degreeVec(0.0f, 10.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                ),
                Keyframe(
                    0.0833f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                ),
                Keyframe(
                    0.125f,
                    KeyframeAnimations.degreeVec(0.0f, -10.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                ),
                Keyframe(
                    0.1667f,
                    KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                    AnimationChannel.Interpolations.LINEAR
                )
            )
        )
        .addAnimation(
            "right_arm", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .build()

    val sitting: AnimationDefinition = AnimationDefinition.Builder.withLength(1.0f)
        .addAnimation(
            "left_arm", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(-22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .addAnimation(
            "left_leg", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(
                    0.0f,
                    KeyframeAnimations.degreeVec(-75.8513f, -20.7048f, -9.0072f),
                    AnimationChannel.Interpolations.LINEAR
                )
            )
        )
        .addAnimation(
            "right_leg", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(
                    0.0f,
                    KeyframeAnimations.degreeVec(-75.8513f, 20.7048f, 9.0072f),
                    AnimationChannel.Interpolations.LINEAR
                )
            )
        )
        .addAnimation(
            "right_arm", AnimationChannel(
                AnimationChannel.Targets.ROTATION,
                Keyframe(0.0f, KeyframeAnimations.degreeVec(-22.5f, 0.0f, 0.0f), AnimationChannel.Interpolations.LINEAR)
            )
        )
        .build()
}
