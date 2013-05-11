Ext.define('Rovachan.model.Thread', {
    extend: 'Ext.data.Model',

    fields: [
        { name: 'id',       type: 'string' },
        { name: 'url',      type: 'string' },
        { name: 'title',    type: 'string' }
    ],

    getId: function()
    {
        return this.get('id');
    },

    getName: function()
    {
        return this.get('id');
    },

    getUrl: function()
    {
        return this.get('url');
    }
});