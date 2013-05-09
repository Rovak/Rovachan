Ext.define("Rovachan.controller.Navigation", {

	extend: 'Ext.app.Controller',

	views: [
		'navigation.Boards'
	],

	refs: [
		{
			ref: 'mainPanel',
			selector: '#main-panels'
		}

	],

	init: function()
	{
		var me = this;

		this.control({
			'navigationBoards' : {
				itemclick: function(view, record) {

					if (!record.get("url")) {
						return;
					}

					var tab = me.getMainPanel().add({
						xtype: 'uxiframe',
						title: record.get("text"),
						src: record.get('url'),
						closable: true
					});
					me.getMainPanel().setActiveTab(tab);
				}
			}
		});
	}
});