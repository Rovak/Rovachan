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
        this.control({
            'navigationBoards' : {
                itemclick: this.openBoard
            }
        });
    },

    /**
     * Open a board in a new tab
     */
    openBoard: function(view, record)
    {
        var me = this;

        if (!record.get("url")) {
            return;
        }

        var tab = this.getMainPanel().add({
            xtype: 'panel',
            title: record.get("text"),
            bodyStyle: 'overflow-y: scroll',
            closable: true,
            loader: {
                autoLoad: true,
                loadMask: {
                    msg: 'Loading Thread...'
                },
                url: env.basePath + 'board/' + record.get('id'),
                renderer: 'html',
                callback: function() {
                    Ext.select('.board .thread a', false, tab.getEl().dom)
                        .on('click', function(ev) {
                            ev.preventDefault();
                            var el = Ext.fly(ev.target);
                            var url = el.getAttribute('href');
                            var json = el.getAttribute('data-thread');
                            me.openThread(url, Ext.create('Rovachan.model.Thread', JSON.parse(json)));
                        });
                }
            }
        });

        this.getMainPanel().setActiveTab(tab);
    },

    /**
     * Open a thread by its given URL
     */
    openThread: function(url, thread)
    {
        var tab = this.getMainPanel().add({
            xtype: 'thread',
            title: 'Thread ' + thread.getId(),
            url: url,
            model: thread
        });

        this.getMainPanel().setActiveTab(tab);
    }
});