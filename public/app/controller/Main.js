
/**
 * Rovachan controller
 */
Ext.define('Rovachan.controller.Main', {

	extend: 'Ext.app.Controller',

	init: function()
	{
		var me = this;

		this.control({
			'button[action=watch-thread]' : {
				click: function(button) {
					var panel = button.up('panel[title=thread]');

					me.watchThread(panel.threadModel.thread);
				}
			}
		});
	},

	/**
	 * Add a thread to the watcher
	 *
	 * @param  {String} thread Thread ID
	 */
	watchThread: function(thread)
	{
		Rovachan.postJson('watcher/add', {
			thread: thread
		});
	}
});