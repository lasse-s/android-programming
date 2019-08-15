










AUI.add(
	'portal-available-languages',
	function(A) {
		var available = {};

		var direction = {};

		

			available['en_US'] = 'englanti (Yhdysvallat)';
			direction['en_US'] = 'ltr';

		

			available['fi_FI'] = 'suomi (Suomi)';
			direction['fi_FI'] = 'ltr';

		

			available['sv_SE'] = 'ruotsi (Ruotsi)';
			direction['sv_SE'] = 'ltr';

		

		Liferay.Language.available = available;
		Liferay.Language.direction = direction;
	},
	'',
	{
		requires: ['liferay-language']
	}
);