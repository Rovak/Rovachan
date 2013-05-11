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
                            me.openThread(Ext.fly(ev.target).getAttribute('href'));
                        });
                }
            }
        });

        this.getMainPanel().setActiveTab(tab);
    },

    /**
     * Open a thread by its given URL
     *
     */
    openThread: function(url)
    {
        var tab = this.getMainPanel().add({
            xtype: 'panel',
            title: 'thread',
            bodyStyle: 'overflow-y: scroll',
            closable: true,
            loader: {
                autoLoad: true,
                loadMask: {
                    msg: 'Loading Thread...'
                },
                url: url,
                renderer: 'html',
                callback: function() {

                }
            }
        });

        this.getMainPanel().setActiveTab(tab);
    }
});