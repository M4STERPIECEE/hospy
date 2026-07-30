import { useState } from 'react';
import type { BookingFormData } from 'src/shared/model';
import { useAppForm } from 'src/shared/form/form-setup';
import { zodValidator } from '@tanstack/zod-form-adapter';
import { z } from 'zod';
import { User, Mail, Phone } from 'lucide-react';
import { Button } from 'src/shared/ui/button';
import { Spinner } from 'src/shared/ui/spinner';
import { BookingSuccess } from './booking-success';
import { BookingSummary } from './booking-summary';

const TIME_OPTIONS = [
    { label: 'Choisir une heure', value: '' },
    ...Array.from({ length: 21 }, (_, i) => {
        const h = String(Math.floor(8 + i * 0.5)).padStart(2, '0');
        const m = i % 2 === 0 ? '00' : '30';
        return { label: `${h}:${m}`, value: `${h}:${m}` };
    }),
];

const SERVICE_OPTIONS = [
    { label: 'Sélectionnez un service', value: '' },
    { label: 'Consultation generale', value: 'Consultation generale' },
    { label: 'Detartrage', value: 'Detartrage' },
    { label: 'Blanchiment', value: 'Blanchiment' },
    { label: 'Implant dentaire', value: 'Implant dentaire' },
    { label: 'Orthodontie', value: 'Orthodontie' },
    { label: 'Urgence', value: 'Urgence' },
];

export const BookingForm = () => {
    const [step, setStep] = useState<'form' | 'summary'>('form');
    const [success, setSuccess] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [isFormSubmitting, setIsFormSubmitting] = useState(false);

    const apiBase = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1';

    const form = useAppForm({
        defaultValues: {
            firstName: '',
            lastName: '',
            email: '',
            phone: '',
            date: '',
            time: '',
            service: '',
        } as BookingFormData,
        // @ts-ignore
        validatorAdapter: zodValidator(),
        onSubmit: async ({ value }) => {
            if (step === 'form') {
                setIsFormSubmitting(true);
                await new Promise(resolve => setTimeout(resolve, 2000));
                setIsFormSubmitting(false);
                setStep('summary');
                return;
            }

            setError(null);
            try {
                const response = await fetch(`${apiBase}/appointments/public`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(value),
                });

                if (!response.ok) {
                    throw new Error('Erreur lors de la réservation');
                }

                setSuccess(true);
                form.reset();
                setTimeout(() => {
                    setSuccess(false);
                    setStep('form');
                }, 5000);
            } catch {
                setError("Impossible de confirmer le rendez-vous. Veuillez réessayer.");
            }
        },
    });

    if (success) {
        return <BookingSuccess onBack={() => { setSuccess(false); setStep('form'); }} />;
    }

    return (
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 text-[var(--text-dark)] animate-[fadeInUp_0.6s_ease-out_0.3s_backwards] max-w-2xl mx-auto overflow-hidden">
            <div
                className="flex transition-transform duration-500 ease-in-out"
                style={{
                    width: '200%',
                    transform: step === 'summary' ? 'translateX(-50%)' : 'translateX(0)'
                }}
            >
                <div className="w-1/2 p-8">
                    <h2 className="font-poppins text-[var(--primary)] text-3xl font-bold mb-8">
                        Réserver un rendez-vous
                    </h2>

                    <form onSubmit={(e) => { e.preventDefault(); e.stopPropagation(); form.handleSubmit(); }}>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                            <form.AppField name="firstName" validators={{ onChange: z.string().min(1, 'Prénom requis') }} children={(field) => <field.InputField label="Prénom" placeholder="Votre prénom" startIcon={<User />} />} />
                            <form.AppField name="lastName" validators={{ onChange: z.string().min(1, 'Nom requis') }} children={(field) => <field.InputField label="Nom" placeholder="Votre nom" startIcon={<User />} />} />
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                            <form.AppField name="email" validators={{ onChange: z.string().email('Email invalide').min(1, 'Email requis') }} children={(field) => <field.InputField label="Email" type="email" placeholder="adresse@exemple.com" startIcon={<Mail />} />} />
                            <form.AppField name="phone" validators={{ onChange: z.string().min(1, 'Téléphone requis') }} children={(field) => <field.InputField label="Téléphone" type="tel" placeholder="06 12 34 56 78" startIcon={<Phone />} />} />
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                            <form.AppField name="date" validators={{ onChange: z.string().min(1, 'Date requise') }} children={(field) => <field.DateField label="Date" fromDate={new Date()} />} />
                            <form.AppField name="time" validators={{ onChange: z.string().min(1, 'Heure requise') }} children={(field: any) => (
                                <field.SelectField label="Heure" placeholder="Sélectionnez une heure" options={TIME_OPTIONS} />
                            )} />
                        </div>

                        <div className="mb-8">
                            <form.AppField name="service" validators={{ onChange: z.string().min(1, 'Service requis') }} children={(field: any) => (
                                <field.SelectField label="Type de soin" placeholder="Sélectionnez un service" options={SERVICE_OPTIONS} />
                            )} />
                        </div>

                        <form.Subscribe selector={(state) => [state.canSubmit]} children={([canSubmit]) => (
                            <div className="flex justify-center mt-15">
                                <Button type="submit" disabled={!canSubmit || isFormSubmitting} className="w-fit px-8 h-12 text-base">
                                    {isFormSubmitting ? <Spinner className="size-5" /> : null}
                                    {isFormSubmitting ? 'Vérification...' : 'Continuer'}
                                </Button>
                            </div>
                        )} />
                    </form>
                </div>

                <div className="w-1/2 p-8">
                    <form.Subscribe selector={(state) => [state.isSubmitting]} children={([isSubmitting]) => (
                        <BookingSummary
                            values={form.state.values}
                            error={error}
                            isSubmitting={isSubmitting}
                            onBack={() => setStep('form')}
                            onConfirm={() => form.handleSubmit()}
                        />
                    )} />
                </div>
            </div>
        </div>
    );
};