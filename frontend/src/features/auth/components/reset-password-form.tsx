import { useState } from 'react';
import { useSearch, useNavigate } from '@tanstack/react-router';
import { useForm } from '@tanstack/react-form';
import { zodValidator } from '@tanstack/zod-form-adapter';
import { z } from 'zod';
import { Button } from 'src/shared/ui/button';
import { Input } from 'src/shared/ui/input';
import { Alert } from 'src/shared/ui/alert';

export const ResetPasswordForm = () => {
    const search = useSearch({ from: '/reset-password' });
    const navigate = useNavigate();
    const [error, setError] = useState<string | null>(null);
    const [info, setInfo] = useState<string | null>(null);

    const apiBase = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';
    const token = (search as { token?: string }).token ?? '';

    const form = useForm({
        defaultValues: {
            newPassword: '',
            confirmPassword: '',
        },
        // @ts-ignore
        validatorAdapter: zodValidator(),
        onSubmit: async ({ value }) => {
            setError(null);
            setInfo(null);

            if (!token) {
                setError('Token manquant. Verifiez le lien recu par email.');
                return;
            }

            if (value.newPassword !== value.confirmPassword) {
                setError('Les mots de passe ne correspondent pas.');
                return;
            }

            try {
                const response = await fetch(`${apiBase}/auth/reset-password`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ token, newPassword: value.newPassword }),
                });

                if (!response.ok) {
                    const data = await response.json().catch(() => ({}));
                    throw new Error(data.message || 'Erreur de reinitialisation.');
                }

                setInfo('Mot de passe mis a jour. Vous pouvez vous connecter.');
                setTimeout(() => navigate({ to: '/login' }), 1200);
            } catch (err) {
                setError(err instanceof Error ? err.message : 'Erreur de reinitialisation.');
            }
        },
    });

    return (
        <div className="bg-white rounded-2xl p-10 shadow-[0_10px_30px_rgba(10,77,104,0.15)] text-[var(--text-dark)]">
            <h2 className="font-poppins text-[var(--primary)] text-3xl font-bold mb-2">
                Reinitialiser le mot de passe
            </h2>
            <p className="text-[var(--primary)]/70 text-[0.95rem] mb-6">
                Saisissez votre nouveau mot de passe pour acceder au tableau de bord.
            </p>

            <form onSubmit={(e) => { e.preventDefault(); e.stopPropagation(); form.handleSubmit(); }}>
                <form.Field name="newPassword" validators={{ onChange: z.string().min(8, 'Mot de passe trop court (8 caracteres minimum).') }} children={(field) => (
                    <div className="mb-4">
                        <label className="block text-[var(--text-dark)] font-semibold mb-2 text-[0.95rem]">
                            Nouveau mot de passe
                        </label>
                        <Input type="password" name={field.name} value={field.state.value} onBlur={field.handleBlur} onChange={(e) => field.handleChange(e.target.value)} className="w-full p-3.5 border-2 border-[var(--border)] rounded-lg text-base transition-all duration-300 focus:outline-none focus:border-[var(--accent)] focus:ring-[3px] focus:ring-[var(--accent)]/10" />
                        {field.state.meta.errors ? (
                            <p className="text-xs text-[var(--danger)] mt-1">{field.state.meta.errors.join(', ')}</p>
                        ) : null}
                    </div>
                )} />

                <form.Field name="confirmPassword" validators={{ onChange: z.string().min(1, 'Veuillez confirmer le mot de passe') }} children={(field) => (
                    <div className="mb-6">
                        <label className="block text-[var(--text-dark)] font-semibold mb-2 text-[0.95rem]">
                            Confirmer le mot de passe
                        </label>
                        <Input type="password" name={field.name} value={field.state.value} onBlur={field.handleBlur} onChange={(e) => field.handleChange(e.target.value)} className="w-full p-3.5 border-2 border-[var(--border)] rounded-lg text-base transition-all duration-300 focus:outline-none focus:border-[var(--accent)] focus:ring-[3px] focus:ring-[var(--accent)]/10" />
                        {field.state.meta.errors ? (
                            <p className="text-xs text-[var(--danger)] mt-1">{field.state.meta.errors.join(', ')}</p>
                        ) : null}
                    </div>
                )} />

                {error && (
                    <Alert variant="destructive" className="mb-4">
                        {error}
                    </Alert>
                )}

                {info && (
                    <Alert className="mb-4">
                        {info}
                    </Alert>
                )}

                <div className="flex justify-center mt-2">
                    <form.Subscribe selector={(state) => [state.canSubmit, state.isSubmitting]} children={([canSubmit, isSubmitting]) => (
                        <Button type="submit" disabled={!canSubmit || isSubmitting} className="w-[70%] py-3 text-lg">
                            {isSubmitting ? 'Mise a jour...' : 'Mettre a jour'}
                        </Button>
                    )} />
                </div>
            </form>
        </div>
    );
};