import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * 一个绑定到 Bukkit 插件生命周期的 CoroutineScope。
 * 插件卸载时会自动取消所有协程任务。
 */
class PluginCoroutineScope : CoroutineScope {

  private val job = SupervisorJob()
  override val coroutineContext = Dispatchers.IO + job

  /**
   * 取消所有协程任务
   */
  fun cancelAll() {
    job.cancel()
  }
}
