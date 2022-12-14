package io.github.bruce0203.chatswearfilter

import org.apache.commons.io.IOUtils
import org.json.*
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern


class Swear(val file: File, val map: HashMap<List<String>, List<String>> = readFile(file)) {

    companion object {
        fun readFile(file: File): HashMap<List<String>, List<String>> {
            val input: String = IOUtils.toString(file.inputStream())
            val json = JSONObject(input)
            val map = LinkedHashMap<List<String>, List<String>>()
            val entryList = json.toMap().values
            for (entry in entryList) {
                val m = (entry as HashMap<*, *>).mapKeys { it.key.toString() }
                map.put(m["words"] as List<String>, m["excludes"] as? List<String>?: ArrayList())
            }
            return map
        }
    }

    fun findSwear(input: String): Boolean {
        val input = input.replace("_", "")
            .replace(Regex("\\d"), "")
        val allMatches: ArrayList<String> = ArrayList()
        map.forEach { (words, excludes) ->
            words.forEach { word ->
                val m: Matcher = Pattern.compile(word, Pattern.CASE_INSENSITIVE)
                    .matcher(input)
                while (m.find()) {
                    if (!excludes.contains(input)) return true
                    allMatches.add(m.group())
                }
            }
        }
        return false
    }

}