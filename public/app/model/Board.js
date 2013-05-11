Ext.define('Rovachan.model.Board', {
    extend: 'Ext.data.Model',

    fields: [
        { name: 'id',       type: 'string' },
        { name: 'url',      type: 'string' },
        { name: 'title',    type: 'string' }
    ],

    getName: function()
    {
        return this.get('id');
    },

    getUrl: function()
    {
        return this.get('url');
    }
});