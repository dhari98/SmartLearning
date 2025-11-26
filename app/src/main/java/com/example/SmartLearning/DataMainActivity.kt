@file:Suppress("PackageName")

package com.example.SmartLearning
/**
 * DataMainActivity
 *
 * A data model used to represent items displayed in the MainActivity list.
 * Each item may include:
 *  - title     → the displayed text (required)
 *  - image     → optional image resource for the item
 *  - id        → unique identifier used for navigation or logic
 *  - customType → optional manual item type override
 *
 * The 'type' property determines how the item should be rendered in the RecyclerView:
 *   1 → Item contains an image
 *   2 → Item without image (text-only item)
 *  -1 → Assigned only when customType is provided
 */
data class DataMainActivity(
    val title: String,
    val image: Int? = null,
    val id: Int,
    val customType: Int = -1 // -1 means no custom type was manually set
) {

    /**
     * Automatically detects the item type based on the provided attributes:
     *
     * Priority:
     *  (1) If customType is set manually → return it directly
     *  (2) If no image exists → return type 2 (text-only item)
     *  (3) Otherwise → return type 1 (item with image)
     *
     * This logic helps the RecyclerView adapter decide which layout to display.
     */
    val type: Int
        get() = when {
            customType != -1 -> customType      // Custom item type overrides auto-detection
            image == null || image == 0 -> 2    // No image → Type 2
            else -> 1                            // Image exists → Type 1
        }
}
