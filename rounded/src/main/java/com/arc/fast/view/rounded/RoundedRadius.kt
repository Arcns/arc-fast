package com.arc.fast.view.rounded

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class RoundedRadius(
    var roundedRadiusTopLeft: Float = 0f,
    var roundedRadiusTopRight: Float = 0f,
    var roundedRadiusBottomLeft: Float = 0f,
    var roundedRadiusBottomRight: Float = 0f
) : Parcelable {
    constructor(roundedRadius: Float) : this(
        roundedRadius, roundedRadius, roundedRadius, roundedRadius
    )

    constructor(
        roundedView: IRoundedView,
    ) : this(
        roundedView._config.radius
    )

    constructor(
        roundedRadius: RoundedRadius,
    ) : this(
        roundedRadius.roundedRadiusTopLeft,
        roundedRadius.roundedRadiusTopRight,
        roundedRadius.roundedRadiusBottomLeft,
        roundedRadius.roundedRadiusBottomRight
    )

    operator fun div(target: Float): RoundedRadius {
        return RoundedRadius(
            roundedRadiusTopLeft / target,
            roundedRadiusTopRight - target,
            roundedRadiusBottomLeft - target,
            roundedRadiusBottomRight - target
        )
    }

    operator fun minus(roundedRadius: RoundedRadius): RoundedRadius {
        return RoundedRadius(
            roundedRadiusTopLeft - roundedRadius.roundedRadiusTopLeft,
            roundedRadiusTopRight - roundedRadius.roundedRadiusTopRight,
            roundedRadiusBottomLeft - roundedRadius.roundedRadiusBottomLeft,
            roundedRadiusBottomRight - roundedRadius.roundedRadiusBottomRight
        )
    }
}