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
     * @param  {[type]} board [description]
     * @return {[type]}       [description]
     */
    openBoard: function(view, record)
    {
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
                listeners: {
                    load: function() {
                        Ext
                            .select('.board .thread a', false, tab.getEl().dom)
                            .on('click', function(ev) {
                                ev.preventDefault(); alert("gas");
                            });
                    }
                }
            }
        });

        this.getMainPanel().setActiveTab(tab);
    }
});