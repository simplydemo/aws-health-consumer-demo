package io.github.simplydemo.utils

class MessageUtils {
    companion object {
        @JvmStatic
        fun isPatternMatched(text: String, patternList: List<String>?): Boolean {
            if (patternList.isNullOrEmpty() || patternList[0].length < 3) return false
            // println("patternList: ${patternList.size} --- $patternList")
            return patternList.any { pattern ->
                text.lowercase().contains(pattern.lowercase())
            }
        }

        @JvmStatic
        fun severityColor(severity: String): String {
            return when (severity) {
                "CRITICAL" -> "#ff0000"
                "HIGH" -> "#ff5c33"
                "WARNING" -> "#ff6600"
                "INFO" -> "#0000ff"
                else -> "#1a1a1a"
            }
        }
    }
}