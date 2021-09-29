import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

return {
	TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
	def zip = it.toPath()

	def fs = FileSystems.newFileSystem(zip, null)
	try {
		def time = FileTime.fromMillis(Long.parseLong('git show -s --format=%ct HEAD'.execute().text.trim())*1000)
		for (Path d : fs.getRootDirectories()) {
			Files.walk(d).forEach {
				try {
					Files.getFileAttributeView(it, BasicFileAttributeView.class).setTimes(time, time, time)
				} catch (e) {}
			}
		}
		def refmap = fs.getPath("yttr-refmap.json")
		def gson = new GsonBuilder().setPrettyPrinting().create()
		def obj = gson.fromJson(new String(Files.readAllBytes(refmap), StandardCharsets.UTF_8), JsonObject.class)
		def sorter
		sorter = {
			List<String> keys = new ArrayList<>()
			// no keySet, can't addAll... thanks google
			for (Map.Entry<String, JsonElement> en : it.entrySet()) {
				keys.add(en.getKey())
			}
			Collections.sort(keys)
			def out = new JsonObject()
			for (String k : keys) {
				def ele = it.get(k)
				if (ele.isJsonObject()) {
					ele = sorter(ele)
				}
				out.add(k, ele)
			}
			return out
		}
		def sortedObj = sorter(obj)
		Files.write(refmap, gson.toJson(sortedObj).getBytes(StandardCharsets.UTF_8))
	} finally {
		fs.close()
	}
}
